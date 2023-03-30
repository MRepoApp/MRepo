package com.sanmer.mrepo

import android.app.Application
import com.sanmer.mrepo.app.Shortcut
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.utils.MediaStoreUtils
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.timber.DebugTree
import com.sanmer.mrepo.utils.timber.ReleaseTree
import com.sanmer.mrepo.works.Works
import timber.log.Timber

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
        app = this

        MediaStoreUtils.init(this)
        NotificationUtils.init(this)

        ModuleManager.init(this)
        RepoManger.init(this)
        Works.init(this)

        Shortcut.push()
    }

    companion object {
        private lateinit var app: App
        val context get() = app
    }
}