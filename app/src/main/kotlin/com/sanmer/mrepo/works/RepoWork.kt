package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.app.Status
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
        if (Status.Cloud.isLoading) {
            Timber.w("getRepo is already loading!")
            return Result.failure()
        } else {
            Status.Cloud.setLoading()
        }

        Timber.i("getRepo: ${RepoManger.enabled}/${RepoManger.all}")
        val out = RepoManger.getRepoAll().map { repo ->
            RepoProvider.getRepo(repo)
        }

        val result = if (out.all { it.isSuccess }) {
            Status.Cloud.setSucceeded()
            Result.success()
        } else {
            if (out.all { it.isFailure }) {
                Status.Cloud.setFailed()
                Result.retry()
            } else {
                Status.Cloud.setSucceeded()
                Result.failure()
            }
        }

        return result
    }
}