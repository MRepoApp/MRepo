package com.sanmer.mrepo.data

import android.content.Context
import com.sanmer.mrepo.data.database.ModuleDatabase
import com.sanmer.mrepo.data.database.entity.toEntity
import com.sanmer.mrepo.data.database.entity.toModule
import com.sanmer.mrepo.data.module.LocalModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object ModuleManager {
    private lateinit var db: ModuleDatabase
    private val moduleDao get() = db.moduleDao()

    val local get() = moduleDao.getLocalCount()

    fun init(context: Context) {
        db = ModuleDatabase.getDatabase(context)
    }

    fun getLocalFlow() = moduleDao.getLocalFlow().map { list ->
        list.map { it.toModule() }
    }

    private suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        moduleDao.getLocalAll().map { it.toModule() }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        moduleDao.insertLocal(value.toEntity())
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        moduleDao.deleteLocalAll()
        moduleDao.insertLocal(list.map { it.toEntity() })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        moduleDao.deleteLocalAll()
    }

    suspend fun getOnlineAll() = RepoManger.getModuleAll()
}