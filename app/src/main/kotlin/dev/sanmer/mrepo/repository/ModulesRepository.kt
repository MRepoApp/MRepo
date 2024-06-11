package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.database.entity.Repo
import dev.sanmer.mrepo.network.runRequest
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
        with(mm.modules) {
            localRepository.deleteLocalAll()
            localRepository.insertLocal(this)
            localRepository.clearUpdatableTag(map { it.id })
        }
    }

    suspend fun getLocal(id: String) = withContext(Dispatchers.IO) {
        val module = mm.getModuleById(id)
        localRepository.insertLocal(module)
    }

    suspend fun getRepoAll(onlyEnable: Boolean = true) =
        localRepository.getRepoAll().filter {
            if (onlyEnable) it.enable else true
        }.map {
            getRepo(it)
        }

    suspend fun getRepo(repo: Repo) = withContext(Dispatchers.IO) {
        runRequest {
            val api = IRepoManager.build(repo.url)
            return@runRequest api.modules.execute()
        }.onSuccess { modulesJson ->
            localRepository.insertRepo(repo.copy(modulesJson))
            localRepository.deleteOnlineByUrl(repo.url)
            localRepository.insertOnline(
                list = modulesJson.modules,
                repoUrl = repo.url
            )
        }.onFailure {
            Timber.e(it, "getRepo: ${repo.url}")
        }
    }
}