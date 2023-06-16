package com.sanmer.mrepo.repository

import com.sanmer.mrepo.api.online.ModulesRepoApi
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.RepoMetadata
import com.sanmer.mrepo.database.entity.toEntity
import com.sanmer.mrepo.di.ApplicationScope
import com.sanmer.mrepo.utils.expansion.runRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModulesRepository @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    init {
        localRepository.getRepoAllAsFlow()
            .distinctUntilChanged()
            .onEach {
                if (it.isEmpty()) return@onEach

                getRepoAll()
            }.launchIn(applicationScope)
    }

    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        suRepository.getModules()
            .onSuccess {
                localRepository.insertLocal(it)
            }.onFailure {
                Timber.e(it, "getLocalAll")
            }
    }

    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        localRepository.getRepoAll()
            .filter { it.enable }
            .map { repo ->
                runRequest {
                    val api = ModulesRepoApi.build(repo.url)
                    return@runRequest api.getModules().execute()
                }.onSuccess { modulesJson ->
                    val new = repo.copy(
                        name = modulesJson.name,
                        size = modulesJson.modules.size,
                        timestamp = modulesJson.timestamp,
                        version = modulesJson.metadata.version,
                        versionCode = modulesJson.metadata.versionCode
                    )

                    if (!new.isCompatible()) {
                        Timber.w("getRepoAll: incompatible repository(${repo.url}), " +
                                "required version >= ${RepoMetadata.current.versionCode}")

                        localRepository.updateRepo(new.copy(enable = false))
                        return@onSuccess
                    } else {
                        localRepository.updateRepo(new)
                    }

                    val list = modulesJson.modules.map { it.toEntity(repo.url) }
                    localRepository.deleteOnlineByUrl(repo.url)
                    localRepository.insertOnline(list)
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
        ) { modulesJson ->
            val new = repo.copy(
                name = modulesJson.name,
                size = modulesJson.modules.size,
                timestamp = modulesJson.timestamp,
                version = modulesJson.metadata.version,
                versionCode = modulesJson.metadata.versionCode
            )

            return@runRequest if (new.isCompatible()) {
                new
            } else {
                new.copy(enable = false)
            }
        }
    }

    suspend fun getUpdate(repoUrl: String, moduleId: String) = withContext(Dispatchers.IO) {
        runRequest {
            val api = ModulesRepoApi.build(repoUrl)
            api.getUpdate(moduleId).execute()
        }
    }
}