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
    fun getLocalAllAsFlow() = localDao.getAllAsFlow().map { data ->
        data.map { it.toModule() }
    }

    fun getLocalWithUpdatableAllAsFlow() = joinDao.getLocalWithUpdatableAll()
        .map { entries ->
            entries.map {
                val module = it.key.toModule()
                val updatable = it.value?.updatable ?: true
                module to updatable
            }
        }

    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        localDao.getAll().map { it.toModule() }
    }

    suspend fun getLocalWithUpdatableOrNull(id: String) = withContext(Dispatchers.IO) {
        joinDao.getLocalWithUpdatable(id)
            .map {
                val module = it.key.toModule()
                val updatable = it.value?.updatable ?: true
                module to updatable
            }.firstOrNull()
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        localDao.insert(LocalModuleEntity(value))
    }

    suspend fun insertLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.insert(list.map { LocalModuleEntity(it) })
    }

    suspend fun deleteLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.delete(list.map { LocalModuleEntity(it) })
    }

    suspend fun deleteLocalAll() = withContext(Dispatchers.IO) {
        localDao.deleteAll()
    }

    suspend fun insertLocalUpdatable(id: String, updatable: Boolean) = withContext(Dispatchers.IO) {
        localDao.insertUpdatable(
            LocalModuleEntity.Updatable(id = id, updatable = updatable)
        )
    }

    suspend fun isLocalUpdatable(id: String) = withContext(Dispatchers.IO) {
        localDao.getUpdatableOrNull(id)?.updatable ?: true
    }

    suspend fun deleteLocalUpdatable(removed: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.deleteUpdatable(
            removed.map {
                LocalModuleEntity.Updatable(id = it.id, updatable = false)
            }
        )
    }

    fun getRepoAllAsFlow() = repoDao.getAllAsFlow()

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        repoDao.getAll()
    }

    suspend fun insertRepo(value: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.insert(value)
    }

    suspend fun deleteRepo(value: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.delete(value)
    }

    fun getOnlineAllAsFlow() = joinDao.getOnlineAllAsFlow().map { entries ->
        entries.map { (module, versions) ->
            module.toJson(
                versions = versions.map { it.toJson() }
            )
        }
    }

    suspend fun getOnlineAllById(id: String) = withContext(Dispatchers.IO) {
        onlineDao.getAllById(id).map { it.toJson() }
    }

    suspend fun insertOnline(repoUrl: String, list: List<OnlineModule>) = withContext(Dispatchers.IO) {
        val modules = list.map {
            OnlineModuleEntity(repoUrl = repoUrl, original = it)
        }
        val versions = list.map { module ->
            module.versions.map {
                VersionItemEntity(repoUrl = repoUrl, id = module.id, original = it)
            }
        }.flatten()

        versionDao.insert(versions)
        onlineDao.insert(modules)
    }

    suspend fun deleteOnlineByUrl(repoUrl: String) = withContext(Dispatchers.IO) {
        versionDao.deleteByUrl(repoUrl)
        onlineDao.deleteByUrl(repoUrl)
    }

    suspend fun getVersionById(id: String) = withContext(Dispatchers.IO) {
        joinDao.getVersionWithRepo(id).values.flatMap { versions ->
            versions.map { it.toJson() }
        }
    }

    suspend fun getVersionByIdWithRepo(id: String) = withContext(Dispatchers.IO) {
        joinDao.getVersionWithRepo(id).mapValues { entry ->
            entry.value.map { it.toJson() }
        }
    }
}