package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.compat.NetworkCompat.runRequest
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.stub.IRepoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModulesRepository @Inject constructor(
    private val localRepository: LocalRepository,
) {
    private val mm get() = Compat.moduleManager

    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        runCatching {
            mm.modules.toList()
        }.onSuccess { modules ->
            val moduleIds = modules.map { it.id }
            val locals = localRepository.getLocalAll()
            val removed = locals.filter { !moduleIds.contains(it.id) }

            localRepository.deleteLocal(removed)
            localRepository.deleteLocalUpdatable(removed)
            localRepository.insertLocal(modules)
        }.onFailure {
            Timber.e(it, "getLocalAll")
        }
    }

    suspend fun getLocal(id: String) = withContext(Dispatchers.IO) {
        runCatching {
            mm.getModuleById(id)
        }.onSuccess {
            localRepository.insertLocal(it)
        }.onFailure {
            Timber.e(it, "getLocal: $id")
        }
    }

    suspend fun getRepoAll(onlyEnable: Boolean = true) =
        localRepository.getRepoAll().filter {
            if (onlyEnable) !it.disable else true
        }.map {
            getRepo(it)
        }

    suspend fun getRepo(repo: RepoEntity) =
        runRequest {
            val api = IRepoManager.build(repo.url)
            api.modules.execute()
        }.onSuccess { modulesJson ->
            localRepository.insertRepo(repo.copy(modulesJson))
            localRepository.deleteOnlineByUrl(repo.url)
            localRepository.insertOnline(repoUrl = repo.url, list = modulesJson.modules)
        }.onFailure {
            Timber.e(it, "getRepo: ${repo.url}")
        }
}