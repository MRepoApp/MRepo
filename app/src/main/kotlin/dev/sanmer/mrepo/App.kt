package dev.sanmer.mrepo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.sanmer.mrepo.app.utils.NotificationUtils
import dev.sanmer.mrepo.compat.NetworkCompat
import dev.sanmer.mrepo.utils.timber.DebugTree
import dev.sanmer.mrepo.utils.timber.ReleaseTree
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
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
        NetworkCompat.setCacheDir(cacheDir)
    }
}