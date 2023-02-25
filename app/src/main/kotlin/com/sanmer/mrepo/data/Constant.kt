package com.sanmer.mrepo.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.database.AppDatabase
import com.sanmer.mrepo.data.database.entity.toEntity
import com.sanmer.mrepo.data.database.entity.toModule
import com.sanmer.mrepo.data.json.Modules
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.provider.repo.RepoLoader.deleteRepo
import com.sanmer.mrepo.provider.repo.RepoLoader.getAllRepo
import com.sanmer.mrepo.provider.repo.RepoLoader.updateRepo
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Constant {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private val localModuleDao get() = db.localModuleDao()

    val local = mutableStateListOf<LocalModule>()
    val localSize get() = local.size
    val online = mutableStateListOf<OnlineModule>()
    val onlineSize get() = online.size

    private val cloud = mutableListOf<Modules>()
    val isReady get() = local.isNotEmpty() || Status.Local.isFinished &&
            cloud.isNotEmpty() || Status.Cloud.isFinished

    fun init(context: Context): AppDatabase {
        db = AppDatabase.getDatabase(context)
        coroutineScope.launch {
            getLocalAll()
            getCloudAll(context)
            getOnline()
        }

        return db
    }

    @JvmName("getOnlineValue")
    suspend fun getOnline() = withContext(Dispatchers.Default) {
        if (online.isNotEmpty()) {
            online.clear()
        }

        val list = mutableListOf<OnlineModule>()

        cloud.forEach { json ->
            json.modules.filter { module ->
                if (module !in list) {
                    module.repoId.update(json.repoId)
                    true
                } else {
                    val old = list.first { it == module }
                    old.repoId.update(json.repoId)
                    old.repoId.sortByDescending { findModule(it, old.id).versionCode }

                    val new = findModule(old.repoId.first(), old.id)
                    new.repoId.addAll(old.repoId)
                    list.update(new)

                    false
                }
            }.let {
                list.addAll(it)
            }
        }

        list.apply {
            online.addAll(this)
        }
    }

    private fun findModule(
        repoId: Long,
        moduleId: String
    ) = cloud.first {
        it.repoId == repoId
    }.modules.first {
        it.id == moduleId
    }

    @Synchronized
    fun insertLocal(value: LocalModule) = coroutineScope.launch(Dispatchers.IO) {
        local.add(value)
        localModuleDao.insert(value.toEntity())
    }

    @Synchronized
    fun updateLocal(value: LocalModule) = coroutineScope.launch(Dispatchers.IO) {
        local.update(value)
        localModuleDao.update(value.toEntity())
    }

    @Synchronized
    fun insertLocal(list: List<LocalModule>) = coroutineScope.launch(Dispatchers.IO) {
        if (local.isNotEmpty()) {
            local.clear()
        }

        local.addAll(list)
        localModuleDao.deleteAll()
        localModuleDao.insert(list.map { it.toEntity() })
    }

    @Synchronized
    fun deleteLocalAll() = coroutineScope.launch(Dispatchers.IO) {
        local.clear()
        localModuleDao.deleteAll()
    }

    private suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        if (local.isNotEmpty()) {
            local.clear()
        }

        localModuleDao.getAll().map { it.toModule() }.apply {
            local.addAll(this)
        }
    }

    @Synchronized
    fun updateCloud(
        context: Context,
        id: Long,
        value: Modules
    ) = coroutineScope.launch(Dispatchers.IO) {
        cloud.update(value)
        context.updateRepo(id, value)
    }

    @Synchronized
    fun deleteCloud(
        context: Context,
        id: Long
    ) = coroutineScope.launch(Dispatchers.IO) {
        cloud.removeIf { it.repoId == id }
        context.deleteRepo(id)
    }

    private suspend fun getCloudAll(context: Context) = withContext(Dispatchers.IO) {
        if (cloud.isNotEmpty()) {
            cloud.clear()
        }

        context.getAllRepo().apply {
            cloud.addAll(this)
        }
    }
}