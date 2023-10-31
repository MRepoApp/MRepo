package com.sanmer.mrepo

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.utils.timber.DebugTree
import com.sanmer.mrepo.utils.timber.ReleaseTree
import com.sanmer.mrepo.works.RepoWork
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val workManger by lazy { WorkManager.getInstance(this) }

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun onCreate() {
        super.onCreate()

        NotificationUtils.init(this)
        NetworkUtils.setCacheDir(cacheDir)

        workManger.enqueue(RepoWork.OneTimeWork)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}