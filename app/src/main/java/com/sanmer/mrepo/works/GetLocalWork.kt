package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.utils.module.ModuleLoader
import timber.log.Timber

class GetLocalWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        return try {
            ModuleLoader.getLocal()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e.message)
            Result.retry()
        }
    }
}