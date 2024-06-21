package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.database.dao.LocalDao
import dev.sanmer.mrepo.database.dao.RepoDao
import dev.sanmer.mrepo.database.entity.local.LocalModuleEntity
import dev.sanmer.mrepo.database.entity.online.RepoEntity
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.online.ModulesJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val repoDao: RepoDao,
    private val localDao: LocalDao
) {
    suspend fun insertRepo(value: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.insertRepo(value)
    }

    suspend fun insertRepo(url: String) = withContext(Dispatchers.IO) {
        repoDao.insertRepo(RepoEntity(url))
    }

    suspend fun deleteRepo(value: RepoEntity) = withContext(Dispatchers.IO) {
        repoDao.deleteRepo(value.url)
    }

    suspend fun updateRepo(repo: RepoEntity, modulesJson: ModulesJson) = withContext(Dispatchers.IO) {
        repoDao.updateRepo(repo, modulesJson)
    }

    fun getRepoAllAsFlow() = repoDao.getRepoAllAsFlow()

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        repoDao.getRepoAll()
    }

    fun getOnlineAllAsFlow() = repoDao.getOnlineAndVersionAllAsFlow().map { entries ->
        entries.map { (module, versions) ->
            module.toJson(
                versions = versions.map { it.toJson() }
            )
        }
    }

    suspend fun getOnlineById(id: String) = withContext(Dispatchers.IO) {
        repoDao.getOnlineById(id).map { it.toJson() }
    }

    suspend fun getVersionAndRepoById(id: String) = withContext(Dispatchers.IO) {
        repoDao.getVersionAndRepoById(id).mapValues { entry ->
            entry.value.map { it.toJson() }
        }
    }

    suspend fun getVersionById(id: String) = withContext(Dispatchers.IO) {
        repoDao.getVersionAndRepoById(id).flatMap { entry ->
            entry.value.map { it.toJson() }
        }
    }

    suspend fun insertLocal(value: LocalModule) = withContext(Dispatchers.IO) {
        localDao.insertLocal(LocalModuleEntity(value))
    }

    suspend fun updateLocal(list: List<LocalModule>) = withContext(Dispatchers.IO) {
        localDao.updateLocal(list)
    }

    suspend fun insertUpdatable(id: String, updatable: Boolean) = withContext(Dispatchers.IO) {
        localDao.insertUpdatable(
            LocalModuleEntity.Updatable(id = id, updatable = updatable)
        )
    }

    fun getLocalAllAsFlow() = localDao.getLocalAllAsFlow().map { data ->
        data.map { it.toModule() }
    }

    fun getLocalAndUpdatableAllAsFlow() = localDao.getLocalAndUpdatableAllAsFlow()
        .map { entries ->
            entries.map {
                val module = it.key.toModule()
                val updatable = it.value?.updatable ?: true
                module to updatable
            }
        }

    suspend fun getLocalAndUpdatableById(id: String) = withContext(Dispatchers.IO) {
        localDao.getLocalAndUpdatableById(id)
            .map {
                val module = it.key.toModule()
                val updatable = it.value?.updatable ?: true
                module to updatable
            }.firstOrNull()
    }

    suspend fun isUpdatable(id: String) = withContext(Dispatchers.IO) {
        localDao.getUpdatable(id)?.updatable ?: true
    }
}