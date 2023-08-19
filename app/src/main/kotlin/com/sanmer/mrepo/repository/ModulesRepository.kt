package com.sanmer.mrepo.repository

import com.sanmer.mrepo.api.online.RepoApi
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.copy
import com.sanmer.mrepo.utils.extensions.runRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModulesRepository @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository
) {
    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        suRepository.getModules()
            .onSuccess {
                localRepository.deleteLocalAll()
                localRepository.insertLocal(it)
            }.onFailure {
                Timber.e(it, "getLocalAll")
            }
    }

    suspend fun getRepoAll() =
        localRepository.getEnableAll().map {
            getRepo(it)
        }

    suspend fun getRepo(repo: Repo) = withContext(Dispatchers.IO) {
        runRequest {
            val api = RepoApi.build(repo.url)
            return@runRequest api.getModules().execute()
        }.onSuccess { modulesJson ->
            localRepository.updateRepo(repo.copy(modulesJson))
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