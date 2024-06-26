package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.compat.NetworkCompat.runRequest
import dev.sanmer.mrepo.database.entity.online.RepoEntity
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
            localRepository.updateLocal(modules)
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
            val api = IRepoManager.create(repo.url)
            api.modules.execute()
        }.onSuccess {
            localRepository.updateRepo(repo, it)
        }.onFailure {
            Timber.e(it, "getRepo: ${repo.url}")
        }

    suspend fun getRepo(repoUrl: String) = getRepo(RepoEntity(repoUrl))
}