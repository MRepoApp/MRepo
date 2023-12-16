package com.sanmer.mrepo.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
        Timber.d("LocalWork: doWork")
        modulesRepository.getLocalAll()

        return Result.success()
    }

    companion object {
        val OneTimeWork = OneTimeWorkRequestBuilder<LocalWork>()
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()
    }
}