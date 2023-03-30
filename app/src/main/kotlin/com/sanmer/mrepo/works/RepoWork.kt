package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.provider.repo.RepoProvider
import timber.log.Timber

class RepoWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        val repos = RepoManger.getRepoAll()
        Timber.i("getRepo: ${repos.filter { it.enable }.size}/${repos.size}")

        val out = repos.map { repo ->
            if (repo.enable) {
                RepoProvider.getRepo(repo)
            } else {
                kotlin.Result.success(null)
            }
        }

        val result = if (out.all { it.isSuccess }) {
            Result.success()
        } else {
            if (out.all { it.isFailure }) {
                Result.retry()
            } else {
                Result.failure()
            }
        }

        return result
    }
}