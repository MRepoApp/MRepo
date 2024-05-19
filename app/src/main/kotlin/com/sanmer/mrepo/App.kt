package com.sanmer.mrepo

import android.app.Application
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.compat.BuildCompat
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.utils.timber.DebugTree
import com.sanmer.mrepo.utils.timber.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import org.lsposed.hiddenapibypass.HiddenApiBypass
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

        if (BuildCompat.atLeastP) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }

        NotificationUtils.init(this)
        NetworkUtils.setCacheDir(cacheDir)
    }
}