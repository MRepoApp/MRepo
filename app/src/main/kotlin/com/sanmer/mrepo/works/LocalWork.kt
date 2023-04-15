package com.sanmer.mrepo.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LocalWork @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val modulesRepository: ModulesRepository
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        val result = modulesRepository.getLocalAll()

        return if (result.isSuccess) {
            Result.success()
        } else {
            if (Config.isRoot) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}