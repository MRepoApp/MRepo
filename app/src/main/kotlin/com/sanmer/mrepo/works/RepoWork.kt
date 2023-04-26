package com.sanmer.mrepo.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class RepoWork @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val modulesRepository: ModulesRepository
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        Timber.d("RepoWork: doWork")
        val result = modulesRepository.getRepoAll()

        return if (result.all { it.isFailure }) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    companion object {
        val OneTimeWork = OneTimeWorkRequestBuilder<RepoWork>()
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
    }
}