package com.sanmer.mrepo.data

import android.content.Context
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.database.AppDatabase
import com.sanmer.mrepo.data.database.entity.toEntity
import com.sanmer.mrepo.data.database.entity.toModule
import com.sanmer.mrepo.data.json.Modules
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.provider.repo.RepoLoader.deleteRepo
import com.sanmer.mrepo.data.provider.repo.RepoLoader.getAllRepo
import com.sanmer.mrepo.data.provider.repo.RepoLoader.updateRepo
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Constant {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private val localModuleDao get() = db.localModuleDao()

    val local = mutableListOf<LocalModule>()
    val localSize get() = local.size
    val online = mutableListOf<OnlineModule>()
    val onlineSize get() = online.size

    val cloud = mutableListOf<Modules>()

    val isReady get() = local.isNotEmpty() || Status.Local.isFinished &&
            cloud.isNotEmpty() || Status.Cloud.isFinished

    fun init(context: Context): AppDatabase {
        db = AppDatabase.getDatabase(context)
        coroutineScope.launch {
            getLocalAll()
            getCloudAll(context)
            updateOnline()
        }

        return db
    }

    suspend fun updateOnline() = withContext(Dispatchers.Default) {
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

        online.forEach {
            if (it !in list) online.remove(it)
        }
        list.forEach {
            online.update(it)
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
        if (local.isEmpty()) {
            local.addAll(list)
        } else {
            local.forEach {
                if (it !in list) local.remove(it)
            }
            list.forEach {
                local.update(it)
            }
        }
        localModuleDao.deleteAll()
        localModuleDao.insert(list.map { it.toEntity() })
    }

    private suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        localModuleDao.getAll().map { it.toModule() }.apply {
            if (local.isEmpty()) {
                local.addAll(this)
            } else {
                local.forEach {
                    if (!contains(it)) local.remove(it)
                }
                forEach {
                    local.update(it)
                }
            }
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
        cloud.find {
            it.repoId == id
        }?.let {
            cloud.remove(it)
        }
        context.deleteRepo(id)
    }

    private suspend fun getCloudAll(context: Context) = withContext(Dispatchers.IO) {
        context.getAllRepo().apply {
            if (cloud.isEmpty()) {
                cloud.addAll(this)
            } else {
                cloud.forEach {
                    if (!contains(it)) cloud.remove(it)
                }
                forEach {
                    cloud.update(it)
                }
            }
        }
    }
}