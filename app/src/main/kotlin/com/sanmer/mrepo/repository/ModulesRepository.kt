package com.sanmer.mrepo.repository

import com.sanmer.mrepo.content.IRepoManager
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.copy
import com.sanmer.mrepo.network.runRequest
import com.sanmer.mrepo.provider.SuProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModulesRepository @Inject constructor(
    private val localRepository: LocalRepository,
    private val suProvider: SuProvider
) {
    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        suProvider.lm.getModules()
            .onSuccess { list ->
                val values = list.map { new ->
                    localRepository.getLocalByIdOrNull(new.id)?.apply {
                        new.ignoreUpdates = ignoreUpdates
                    }
                    new
                }
                localRepository.deleteLocalAll()
                localRepository.insertLocal(values)
            }.onFailure {
                Timber.e(it, "getLocalAll")
            }
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