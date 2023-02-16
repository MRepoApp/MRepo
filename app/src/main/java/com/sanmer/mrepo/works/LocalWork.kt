package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.data.provider.local.LocalLoader
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
            LocalLoader.getLocalAll()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e.message)
            Result.retry()
        }
    }
}