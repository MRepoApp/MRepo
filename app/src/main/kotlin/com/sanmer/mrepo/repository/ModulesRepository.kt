package com.sanmer.mrepo.repository

import com.sanmer.mrepo.api.online.ModulesRepoApi
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toEntity
import com.sanmer.mrepo.utils.expansion.runRequest
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
    suspend fun getLocalAll() = suRepository.getModules()
        .onSuccess {
            localRepository.insertLocal(it)
        }.onFailure {
            Timber.e(it, "getLocalAll")
        }

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        localRepository.getRepoAll().map { repo ->
            runRequest {
                val api = ModulesRepoApi.build(repo.url)
                return@runRequest api.getModules().execute()
            }.onSuccess { data ->
                val new = repo.copy(
                    name = data.name,
                    size = data.modules.size,
                    timestamp = data.timestamp
                )
                localRepository.updateRepo(new)

                val list = data.modules.map { it.toEntity(repo.url) }
                localRepository.insertOnline(list)
            }.onFailure {
                Timber.d(it, "getRepoAll: ${repo.url}")
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
            repo.copy(
                name = it.name,
                size = it.modules.size,
                timestamp = it.timestamp
            )
        }
    }

    suspend fun getUpdate(repoUrl: String, moduleId: String) = withContext(Dispatchers.IO) {
        runRequest {
            val api = ModulesRepoApi.build(repoUrl)
            api.getUpdate(moduleId).execute()
        }
    }
}