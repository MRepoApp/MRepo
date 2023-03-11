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
        return try {
            LocalProvider.getLocalAll()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e.message)
            if (EnvProvider.isRoot) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}