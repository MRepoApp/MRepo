package com.sanmer.mrepo.provider.repo

import android.content.Context
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.Repository
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.json.Modules
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.json.Update
import com.sanmer.mrepo.utils.MediaStoreUtils.toFileDir
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

object RepoLoader {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter<Modules>()

    fun getRepoAll(
        context: Context
    ) = coroutineScope.launch(Dispatchers.IO) {
        if (Status.Cloud.isLoading) {
            Timber.w("getRepo is already loading!")
            return@launch
        } else {
            Status.Cloud.setLoading()
        }

        Timber.i("getRepo: ${Repository.enabledRepoSize}/${Repository.repoSize}")
        val out = Repository.getAll().map { repo ->
            getRepo(context, repo)
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

        if (Status.Cloud.isSucceeded) {
            Constant.getOnline()
        }
    }

    suspend fun getRepo(context: Context, repo: Repo) = getRepo(repo)
        .onSuccess {
            Constant.updateCloud(context, repo.id, it.copy(repoId = repo.id))
            Repository.update(repo.copy(
                name = it.name,
                size = it.modules.size,
                timestamp = it.timestamp
            ))
        }
        .onFailure {
            Timber.e("getRepo: ${it.message}")
        }

    private suspend fun getRepo(repo: Repo) = if (repo.enable) {
        getRepo(repo.url)
    } else {
        Result.failure(Exception("${repo.name} is disabled!"))
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
                    Result.failure(Exception("The data is null!"))
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
            return Result.failure(Exception("The repoId is empty!"))
        } else {
            val result = module.repoId.map { id ->
                val repo = Repository.repo.first { it.id == id }
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
                    Result.failure(Exception("The data is null!"))
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

    private fun Long.toDir() = "repositories/${this}.json"

    private fun Context.getRepo(id: Long): Modules? {
        val json = filesDir.resolve(id.toDir())

        return if (json.exists()) {
            adapter.fromJson(json.readText())
        } else {
            null
        }
    }

    fun Context.updateRepo(
        id: Long,
        value: Modules
    ) = toFileDir(adapter.toJson(value), id.toDir())

    fun Context.deleteRepo(id: Long) {
        val json = filesDir.resolve(id.toDir())
        json.delete()
    }

    suspend fun Context.getAllRepo() = withContext(Dispatchers.Default) {
        mutableListOf<Modules>().apply {
            Repository.getAll().forEach { repo ->
                if (repo.enable) {
                    getRepo(repo.id)?.let {
                        add(it.copy(repoId = repo.id))
                    }
                }
            }
        }
    }
}