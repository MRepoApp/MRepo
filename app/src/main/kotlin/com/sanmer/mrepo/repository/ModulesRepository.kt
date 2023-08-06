package com.sanmer.mrepo.repository

import com.sanmer.mrepo.api.online.ModulesRepoApi
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

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        localRepository.getEnableAll().map { repo ->
            runRequest {
                val api = ModulesRepoApi.build(repo.url)
                return@runRequest api.getModules().execute()
            }.onSuccess { modulesJson ->
                val new = repo.copy(modulesJson)
                if (!new.isCompatible) {
                    localRepository.updateRepo(new.copy(enable = false))
                    return@onSuccess
                } else {
                    localRepository.updateRepo(new)
                }

                localRepository.deleteOnlineByUrl(repo.url)
                localRepository.insertOnline(
                    list = modulesJson.modules,
                    repoUrl = repo.url
                )
            }.onFailure {
                Timber.e(it, "getRepoAll: ${repo.url}")
            }
        }
    }

    suspend fun getRepo(repo: Repo) = withContext(Dispatchers.IO) {
        runRequest(
            run = {
                val api = ModulesRepoApi.build(repo.url)
                api.getModules().execute()
            }
        ) {
            val new = repo.copy(it)
            if (new.isCompatible) new else new.copy(enable = false)
        }
    }
}