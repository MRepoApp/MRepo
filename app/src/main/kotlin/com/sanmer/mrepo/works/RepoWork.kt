package com.sanmer.mrepo.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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
        val result = modulesRepository.getRepoAll()

        return if (result.all { it.isFailure }) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}