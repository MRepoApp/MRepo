package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
        RepoProvider.getRepoAll().onSuccess {
            return Result.success()
        }.onFailure {
            return Result.retry()
        }

        return Result.failure()
    }
}