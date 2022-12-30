package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object Works {
    private lateinit var workManager: WorkManager
    private const val TAG_LOCAL = "LOCAL"
    private const val TAG_REPO = "REPO"

    private val RepoPeriodicWork = PeriodicWorkRequestBuilder<GetRepoWork>(12, TimeUnit.HOURS)
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .addTag(TAG_REPO)
        .build()

    private val LocalPeriodicWork = PeriodicWorkRequestBuilder<GetLocalWork>(12, TimeUnit.HOURS)
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .addTag(TAG_LOCAL)
        .build()

    fun init(context: Context) {
        workManager = WorkManager.getInstance(context)
    }

    fun start() {
        workManager.enqueue(listOf(RepoPeriodicWork, LocalPeriodicWork))
    }
}