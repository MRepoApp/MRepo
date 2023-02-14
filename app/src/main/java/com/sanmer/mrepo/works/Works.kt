package com.sanmer.mrepo.works

import android.content.Context
import androidx.work.*
import com.sanmer.mrepo.app.Config
import timber.log.Timber
import java.util.concurrent.TimeUnit

object Works {
    private lateinit var workManager: WorkManager
    private const val PERIODIC = "PERIODIC"
    private const val ONETIME = "ONETIME"

    private val RepoPeriodicWork get() = PeriodicWorkRequestBuilder<RepoWork>(
        repeatInterval = Config.tasksPeriodCount,
        repeatIntervalTimeUnit = Config.tasksPeriodUnit
    )
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .addTag(PERIODIC)
        .build()

    private val RepoOneTimeWork = OneTimeWorkRequestBuilder<RepoWork>()
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .addTag(ONETIME)
        .build()

    private val LocalPeriodicWork get() = PeriodicWorkRequestBuilder<LocalWork>(
        repeatInterval = Config.tasksPeriodCount,
        repeatIntervalTimeUnit = Config.tasksPeriodUnit
    )
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .addTag(PERIODIC)
        .build()

    private val LocalOneTimeWork = OneTimeWorkRequestBuilder<LocalWork>()
        .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
        .addTag(ONETIME)
        .build()

    fun init(context: Context) {
        workManager = WorkManager.getInstance(context)
    }

    private fun setOneTimeTasks() {
        Timber.i("Works: setOneTimeTasks")
        workManager.enqueue(listOf(RepoOneTimeWork, LocalOneTimeWork))
    }

    fun setPeriodTasks() {
        Timber.i("Works: setPeriodTasks")
        val duration = RepoPeriodicWork.workSpec.intervalDuration
        Timber.d("Works: duration=$duration")
        workManager.enqueue(listOf(RepoPeriodicWork, LocalPeriodicWork))
    }

    fun cancelPeriodTasks() {
        Timber.i("Works: cancelPeriodTasks")
        workManager.getWorkInfoById(RepoPeriodicWork.id).cancel(false)
        workManager.getWorkInfoById(LocalPeriodicWork.id).cancel(false)
    }

    fun resetPeriodTasks() {
        cancelPeriodTasks()
        setPeriodTasks()
    }

    fun setTasks() {
        if (Config.checkModulesUpdate) {
            setPeriodTasks()
        } else {
            setOneTimeTasks()
        }
    }
}