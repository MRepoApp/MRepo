package com.sanmer.mrepo.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.data.database.AppDatabase
import com.sanmer.mrepo.data.database.toEntity
import com.sanmer.mrepo.data.database.toModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.OnlineModule
import kotlinx.coroutines.*

object Constant {
    private val job = Job()
    private val coroutineScope = CoroutineScope(job)
    private lateinit var db: AppDatabase
    private val localModuleDao get() = db.localModuleDao()
    private val onlineModuleDao get() = db.onlineModuleDao()

    val local = mutableStateListOf<LocalModule>()
    val online = mutableStateListOf<OnlineModule>()

    val isReady get() = local.isNotEmpty() && online.isNotEmpty() ||
            online.isNotEmpty() && Status.Local.isSucceeded

    fun init(context: Context): AppDatabase {
        db = AppDatabase.getDatabase(context)
        getAll()

        return db
    }

    fun insertLocal(value: LocalModule) {
        coroutineScope.launch(Dispatchers.IO) {
            local.add(value)
            localModuleDao.insert(value.toEntity())
        }
    }

    fun insertLocal(list: List<LocalModule>) {
        coroutineScope.launch(Dispatchers.IO) {
            local.clear()
            local.addAll(list)
            localModuleDao.deleteAll()
            localModuleDao.insert(list.map { it.toEntity() })
        }
    }

    suspend fun updateLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localModuleDao.update(list.map { it.toEntity() })
    }

    fun insertOnline(list: List<OnlineModule>) {
        coroutineScope.launch(Dispatchers.IO) {
            online.clear()
            online.addAll(list)
            onlineModuleDao.deleteAll()
            onlineModuleDao.insert(list.map { it.toEntity() })
        }
    }

    suspend fun updateOnline(list: List<OnlineModule>) = withContext(Dispatchers.IO) {
        onlineModuleDao.update(list.map { it.toEntity() })
    }

    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        localModuleDao.getAll().map { it.toModule() }.let {
            local.clear()
            local.addAll(it)
            it
        }
    }

    suspend fun getOnlineAll() = withContext(Dispatchers.IO) {
        onlineModuleDao.getAll().map { it.toModule() }.let {
            online.clear()
            online.addAll(it)
            it
        }
    }

    fun getAll() {
        coroutineScope.launch(Dispatchers.IO) {
            getLocalAll()
            getOnlineAll()
        }
    }

    fun filterLocal(key: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val list = withContext(Dispatchers.IO) {
                localModuleDao.getAll()
            }

            local.clear()
            local.addAll(
                list.filter {
                    val target = it.name + it.author + it.description
                    key.lowercase() in target.lowercase()
                }.map { it.toModule() }
            )
        }
    }

    fun filterOnline(key: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val list = withContext(Dispatchers.IO) {
                onlineModuleDao.getAll()
            }

            online.clear()
            online.addAll(
                list.filter {
                    val target = it.name + it.author + it.description
                    key.lowercase() in target.lowercase()
                }.map { it.toModule() }
            )
        }
    }
}