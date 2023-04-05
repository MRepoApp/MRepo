package com.sanmer.mrepo.provider.repo

import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.database.entity.toEntity
import com.sanmer.mrepo.data.module.OnlineModule
import com.sanmer.mrepo.utils.expansion.runRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object RepoProvider {
    suspend fun getRepoAll() = withContext(Dispatchers.IO) {
        val repos = RepoManger.getRepoAll()
        Timber.i("getRepo: ${repos.filter { it.enable }.size}/${repos.size}")

        val result = repos.map {
            return@map if (it.enable) {
                getRepo(it)
            } else {
                Result.failure(RuntimeException("${it.name} is disabled!"))
            }
        }

        return@withContext if (result.all { it.isFailure }) {
            Result.failure(result.first().exceptionOrNull()!!)
        } else {
            Result.success(result.mapNotNull { it.getOrNull() })
        }
    }
    suspend fun getRepo(repo: Repo) = withContext(Dispatchers.IO) {
        runRequest {
            val api = RepoService.create(repo.url)
            return@runRequest api.getModules().execute()
        }.onSuccess { result ->
            val new = repo.copy(
                name = result.name,
                size = result.modules.size,
                timestamp = result.timestamp
            )
            RepoManger.updateRepo(new)

            val list = result.modules.map { it.toEntity(repo.url) }
            RepoManger.insertModule(list)

        }.onFailure {
            Timber.e("getRepo: ${it.message}")
        }
    }

    suspend fun getUpdate(module: OnlineModule) = withContext(Dispatchers.IO) {
        if (module.repoUrls.isEmpty()) {
            return@withContext Result.failure(NoSuchElementException("The repoId is empty!"))
        }

        val result = module.repoUrls.map { url ->
            runRequest {
                val api = RepoService.create(url)
                api.getUpdate(module.id).execute()
            }.onSuccess {
                return@map Result.success(it.copy(repoUrl = url))
            }.onFailure {
                Timber.d("getUpdate: ${it.message}")
            }
        }

        return@withContext if (result.all { it.isFailure }) {
            Result.failure(result.first().exceptionOrNull()!!)
        } else {
            Result.success(result.mapNotNull { it.getOrNull() })
        }
    }
}