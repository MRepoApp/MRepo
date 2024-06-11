package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.database.dao.JoinDao
import dev.sanmer.mrepo.database.dao.LocalDao
import dev.sanmer.mrepo.database.dao.OnlineDao
import dev.sanmer.mrepo.database.dao.RepoDao
import dev.sanmer.mrepo.database.dao.VersionDao
import dev.sanmer.mrepo.database.entity.LocalModuleEntity
import dev.sanmer.mrepo.database.entity.OnlineModuleEntity
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.database.entity.VersionItemEntity
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.utils.extensions.merge
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
        localDao.insert(LocalModuleEntity(value))
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.insert(list.map { LocalModuleEntity(it) })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        localDao.deleteAll()
    }

    suspend fun insertUpdatableTag(id: String, updatable: Boolean) = withContext(Dispatchers.IO) {
        localDao.insertUpdatableTag(
            LocalModuleEntity.Updatable(
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

    suspend fun insertRepo(value: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.insert(value)
    }

    suspend fun deleteRepo(value: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.delete(value)
    }

    fun getOnlineAllAsFlow() = joinDao.getOnlineAllAsFlow().map { list ->
        val values = mutableListOf<OnlineModule>()
        list.forEach { entity ->
            val new = entity.toModule()
            if (values.contains(new)) {
                val old = values.first { it.id == new.id }
                if (new.versionCode > old.versionCode) {
                    values.remove(old)
                    values.add(new.copy(versions = old.versions))
                }
            } else {
                val versions = getVersionById(new.id)
                values.add(new.copy(versions = versions))
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
        val modules = list.map {
            OnlineModuleEntity(
                original = it,
                repoUrl = repoUrl
            )
        }

        val versions = list.map { module ->
            module.versions.map {
                VersionItemEntity(
                    original = it,
                    id = module.id,
                    repoUrl = repoUrl
                )
            }
        }.merge()

        versionDao.insert(versions)
        onlineDao.insert(modules)
    }

    suspend fun deleteOnlineByUrl(repoUrl: String) = withContext(Dispatchers.IO) {
        versionDao.deleteByUrl(repoUrl)
        onlineDao.deleteByUrl(repoUrl)
    }

    suspend fun getVersionById(id: String) = withContext(Dispatchers.IO) {
        joinDao.getVersionById(id).map { it.toItem() }
    }
}