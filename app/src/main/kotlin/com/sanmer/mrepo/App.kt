package com.sanmer.mrepo

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.utils.timber.DebugTree
import com.sanmer.mrepo.utils.timber.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration get() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

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
    }
}