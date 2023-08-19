package com.sanmer.mrepo.repository

import androidx.compose.runtime.toMutableStateList
import com.sanmer.mrepo.database.dao.JoinDao
import com.sanmer.mrepo.database.dao.LocalDao
import com.sanmer.mrepo.database.dao.OnlineDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.dao.VersionDao
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toEntity
import com.sanmer.mrepo.database.entity.toItem
import com.sanmer.mrepo.database.entity.toModule
import com.sanmer.mrepo.di.ApplicationScope
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.utils.extensions.merge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val repoDao: RepoDao,
    private val onlineDao: OnlineDao,
    private val versionDao: VersionDao,
    private val localDao: LocalDao,
    private val joinDao: JoinDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    private var _online = listOf<OnlineModule>()
    private var _local = listOf<LocalModule>()
    val online get() = _online.toMutableStateList()
    val local get() = _local.toMutableStateList()

    init {
        getLocalAllAsFlow()
            .onEach { list ->
                _local = list
                Timber.d("update local: ${list.size}")
            }.launchIn(applicationScope)

        getOnlineAllAsFlow()
            .onEach { list ->
                _online = list
                Timber.d("update online: ${list.size}")
            }.launchIn(applicationScope)
    }

    private fun getLocalAllAsFlow() = localDao.getAllAsFlow().map { list ->
        list.map { it.toModule() }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        localDao.insert(value.toEntity())
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.insert(list.map { it.toEntity() })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        localDao.deleteAll()
    }

    fun getRepoAllAsFlow() = repoDao.getAllAsFlow()

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        repoDao.getAll()
    }

    suspend fun getRepoByUrl(url: String) = withContext(Dispatchers.IO) {
        repoDao.getByUrl(url)
    }

    suspend fun insertRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.insert(value)
    }

    suspend fun deleteRepo(value: Repo) = withContext(Dispatchers.IO) {
        repoDao.delete(value)
    }

    private fun getOnlineAllAsFlow() = joinDao.getOnlineAllAsFlow().map { list ->
        val values = mutableListOf<OnlineModule>()
        list.forEach { entity ->
            val new = entity.toModule()

            if (new in values) {
                val old = values.first { it.id == new.id }
                if (new.versionCode > old.versionCode) {
                    values.remove(old)
                    values.add(new.copy(versions = old.versions))
                }
            } else {
                values.add(
                    new.copy(versions = getVersionById(new.id))
                )
            }
        }

        return@map values
    }

    suspend fun getOnlineByIdAndUrl(id: String, repoUrl: String) = withContext(Dispatchers.IO) {
        joinDao.getOnlineByIdAndUrl(id, repoUrl).toModule()
    }

    private suspend fun getVersionById(id: String) = withContext(Dispatchers.IO) {
        joinDao.getVersionById(id).map { it.toItem() }
    }

    suspend fun insertOnline(list: List<OnlineModule>, repoUrl: String) = withContext(Dispatchers.IO) {
        val versions = list.map { module ->
            module.versions.map {
                it.toEntity(
                    id = module.id,
                    repoUrl = repoUrl
                )
            }
        }.merge()

        versionDao.insert(versions)
        onlineDao.insert(list.map { it.toEntity(repoUrl) })
    }

    suspend fun deleteOnlineByUrl(repoUrl: String) = withContext(Dispatchers.IO) {
        versionDao.deleteByUrl(repoUrl)
        onlineDao.deleteByUrl(repoUrl)
    }
}