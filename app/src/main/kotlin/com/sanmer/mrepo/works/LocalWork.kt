package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.local.LocalProvider
import timber.log.Timber

class LocalWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        LocalProvider.getLocalAll().onSuccess {
            return Result.success()
        }.onFailure {
            return if (EnvProvider.isRoot) {
                Result.retry()
            } else {
                Result.failure()
            }
        }

        return Result.failure()
    }
}