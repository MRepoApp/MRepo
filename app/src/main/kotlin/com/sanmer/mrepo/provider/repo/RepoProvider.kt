package com.sanmer.mrepo.provider.repo

import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.CloudManager
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.utils.expansion.runRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

object RepoProvider {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Synchronized
    fun getRepoAll() = coroutineScope.launch {
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

    suspend fun getRepo(repo: Repo) = withContext(Dispatchers.IO) {
        if (!repo.enable) {
            return@withContext Result.failure(RuntimeException("${repo.name} is disabled!"))
        }

        runRequest {
            val api = RepoService.create(repo.url)
            return@runRequest api.getModules().execute()
        }.onSuccess {
            CloudManager.updateById(
                id = repo.id,
                value = it.copy(repoId = repo.id)
            )

            RepoManger.update(
                repo.copy(
                    name = it.name,
                    size = it.modules.size,
                    timestamp = it.timestamp
                )
            )
        }.onFailure {
            Timber.e("getRepo: ${it.message}")
        }
    }

    suspend fun getUpdate(module: OnlineModule) = withContext(Dispatchers.IO) {
        if (module.repoId.isEmpty()) {
            return@withContext Result.failure(NoSuchElementException("The repoId is empty!"))
        }

        val result = module.repoId.map { id ->
            val repo = RepoManger.getById(id)!!

            runRequest {
                val api = RepoService.create(repo.url)
                api.getUpdate(module.id).execute()
            }.onSuccess {
                return@map Result.success(it.copy(repoId = repo.id))
            }.onFailure {
                Timber.d("getUpdate: ${it.message}")
            }
        }

        return@withContext if (result.all { it.isFailure }) {
            Result.failure(result.first().exceptionOrNull()!!)
        } else {
            Result.success(result.map { it.getOrNull() })
        }
    }
}