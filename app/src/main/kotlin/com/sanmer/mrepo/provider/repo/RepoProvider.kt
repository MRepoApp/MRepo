package com.sanmer.mrepo.provider.repo

import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.CloudManager
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.json.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

object RepoProvider {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Synchronized
    fun getRepoAll() = coroutineScope.launch(Dispatchers.IO) {
        if (Status.Cloud.isLoading) {
            Timber.w("getRepo is already loading!")
            return@launch
        } else {
            Status.Cloud.setLoading()
        }

        Timber.i("getRepo: ${RepoManger.enabled}/${RepoManger.all}")
        val out = RepoManger.getAll().map { repo ->
            getRepo(repo)
        }

        if (out.all { it.isSuccess }) {
            Status.Cloud.setSucceeded()
        } else {
            if (out.all { it.isFailure }) {
                Status.Cloud.setFailed()
            } else {
                Status.Cloud.setSucceeded()
            }
        }
    }

    suspend fun getRepo(repo: Repo) = if (repo.enable) {
        getRepo(repo.url).onSuccess {
            CloudManager.updateById(
                id = repo.id,
                value = it.copy(repoId = repo.id)
            )

            RepoManger.update(repo.copy(
                name = it.name,
                size = it.modules.size,
                timestamp = it.timestamp
            ))
        }.onFailure {
            Timber.e("getRepo: ${it.message}")
        }
    } else {
        Result.failure(RuntimeException("${repo.name} is disabled!"))
    }

    private suspend fun getRepo(repoUrl: String) = withContext(Dispatchers.IO) {
        try {
            val api = RepoService.create(repoUrl)
            val response = api.getModules().execute()

            if (response.isSuccessful) {
                val data = response.body()
                return@withContext if (data != null) {
                    Result.success(data)
                }else {
                    Result.failure(IllegalArgumentException("The data is null!"))
                }
            } else {
                val errorBody = response.errorBody()
                val error = errorBody?.string()

                return@withContext Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun getUpdate(module: OnlineModule): Result<List<Update?>> {
        if (module.repoId.isEmpty()) {
            return Result.failure(IllegalArgumentException("The repoId is empty!"))
        } else {
            val result = module.repoId.map { id ->
                val repo = RepoManger.getById(id)!!
                getUpdate(
                    repo = repo,
                    id = module.id
                ).onFailure {
                    Timber.d("getUpdate: ${it.message}")
                }
            }

            return if (result.all { it.isFailure }) {
                Result.failure(result.first().exceptionOrNull()!!)
            } else {
                Result.success(result.map { it.getOrNull() })
            }
        }
    }

    private suspend fun getUpdate(repo: Repo, id: String) = withContext(Dispatchers.IO) {
        try {
            val api = RepoService.create(repo.url)
            val response = api.getUpdate(id).execute()

            if (response.isSuccessful) {
                val data = response.body()
                return@withContext if (data != null) {
                    Result.success(data.copy(repoId = repo.id))
                }else {
                    Result.failure(IllegalArgumentException("The data is null!"))
                }
            } else {
                val errorBody = response.errorBody()
                val error = errorBody?.string()

                return@withContext Result.failure(RuntimeException(error))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}