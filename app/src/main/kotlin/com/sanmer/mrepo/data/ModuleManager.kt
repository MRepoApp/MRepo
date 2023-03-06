package com.sanmer.mrepo.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.database.AppDatabase
import com.sanmer.mrepo.data.database.entity.toEntity
import com.sanmer.mrepo.data.database.entity.toModule
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ModuleManager {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private val localModuleDao get() = db.localModuleDao()

    var local by mutableStateOf(0)
        private set
    var online by mutableStateOf(0)
        private set

    val isReady get() = local != 0 || Status.Local.isFinished &&
            online != 0 || Status.Cloud.isFinished

    fun init(context: Context): AppDatabase {
        db = AppDatabase.getDatabase(context)
        coroutineScope.launch {
            getLocalAll()
            getOnlineAll()
        }

        return db
    }

    suspend fun getOnlineAll() = withContext(Dispatchers.Default) {
        val list = mutableListOf<OnlineModule>()

        CloudManager.getAll().forEach { json ->
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

        return@withContext list.apply {
            online = size
        }
    }

    private fun findModule(
        repoId: Long,
        moduleId: String
    ) = CloudManager.getById(repoId)!!.modules.first { it.id == moduleId }

    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        localModuleDao.getAll().map { it.toModule() }.apply {
            local = size
        }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        localModuleDao.insert(value.toEntity())
    }

    suspend fun updateLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        localModuleDao.update(value.toEntity())
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localModuleDao.deleteAll()
        localModuleDao.insert(list.map { it.toEntity() })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        localModuleDao.deleteAll()
    }
}