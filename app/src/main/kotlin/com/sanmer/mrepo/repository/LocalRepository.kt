package com.sanmer.mrepo.repository

import com.sanmer.mrepo.database.dao.JoinDao
import com.sanmer.mrepo.database.dao.LocalDao
import com.sanmer.mrepo.database.dao.OnlineDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.dao.VersionDao
import com.sanmer.mrepo.database.entity.LocalModuleUpdatable
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toEntity
import com.sanmer.mrepo.database.entity.toItem
import com.sanmer.mrepo.database.entity.toModule
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.utils.extensions.merge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val repoDao: RepoDao,
    private val onlineDao: OnlineDao,
    private val versionDao: VersionDao,
    private val localDao: LocalDao,
    private val joinDao: JoinDao
) {
    fun getLocalAllAsFlow() = localDao.getAllAsFlow().map { list ->
        list.map { it.toModule() }
    }

    suspend fun getLocalByIdOrNull(id: String) = withContext(Dispatchers.IO) {
        localDao.getByIdOrNull(id)?.toModule()
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

    suspend fun insertUpdatableTag(id: String, updatable: Boolean) = withContext(Dispatchers.IO) {
        localDao.insertUpdatableTag(
            LocalModuleUpdatable(
                id = id,
                updatable = updatable
            )
        )
    }

    suspend fun hasUpdatableTag(id: String) = withContext(Dispatchers.IO) {
        localDao.hasUpdatableTagOrNull(id)?.updatable ?: true
    }

    suspend fun clearUpdatableTag(new: List<String>) = withContext(Dispatchers.IO) {
        val removed = localDao.getUpdatableTagAll().filter { it.id !in new }
        localDao.deleteUpdatableTag(removed)
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

    fun getOnlineAllAsFlow() = joinDao.getOnlineAllAsFlow().map { list ->
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

    suspend fun getOnlineAllById(id: String) = withContext(Dispatchers.IO) {
        onlineDao.getAllById(id).map { it.toModule() }
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

    suspend fun getVersionById(id: String) = withContext(Dispatchers.IO) {
        joinDao.getVersionById(id).map { it.toItem() }
    }
}