package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.*
import com.sanmer.mrepo.provider.EnvProvider
import java.util.concurrent.TimeUnit

object Works {
    private lateinit var workManager: WorkManager

    private val RepoOneTimeWork = OneTimeWorkRequestBuilder<RepoWork>()
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    private val LocalOneTimeWork = OneTimeWorkRequestBuilder<LocalWork>()
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .build()

    fun init(context: Context) {
        workManager = WorkManager.getInstance(context)
    }

    fun start() {
        EnvProvider.onRoot {
            workManager.enqueue(listOf(RepoOneTimeWork, LocalOneTimeWork))
        }.onNonRoot {
            workManager.enqueue(RepoOneTimeWork)
        }
    }
}