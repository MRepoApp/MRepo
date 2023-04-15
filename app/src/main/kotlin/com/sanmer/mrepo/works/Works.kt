package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.*
import com.sanmer.mrepo.app.Config
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Works @Inject constructor(
    @ApplicationContext  private val context: Context
) {
    private val workManager by lazy { WorkManager.getInstance(context) }

    private val repoOneTimeWork = OneTimeWorkRequestBuilder<RepoWork>()
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    private val localOneTimeWork = OneTimeWorkRequestBuilder<LocalWork>()
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .build()

    fun start() = when {
        Config.isRoot -> workManager.enqueue(listOf(repoOneTimeWork, localOneTimeWork))
        Config.isNonRoot -> workManager.enqueue(repoOneTimeWork)
        else -> null
    }
}