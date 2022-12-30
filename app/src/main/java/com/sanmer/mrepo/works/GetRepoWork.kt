package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.api.RepoApi
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.app.status.Event
import com.sanmer.mrepo.data.Constant
import timber.log.Timber

class GetRepoWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        Status.Online.event = Event.LOADING

        return try {
            val response = RepoApi.getModules().execute()

            if (response.isSuccessful) {
                val data = response.body()
                data?.apply {
                    Status.Online.timestamp = timestamp
                    Constant.insertOnline(modules)
                }

                Status.Online.event = Event.SUCCEEDED
                Result.success()
            } else {
                val errorBody = response.errorBody()
                val error = errorBody?.string()

                Timber.e("getRepo: $error")
                Status.Online.event = Event.FAILED
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.e("getRepo: ${e.message}")
            Status.Online.event = Event.FAILED
            Result.retry()
        }
    }
}