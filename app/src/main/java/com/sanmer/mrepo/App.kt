package com.sanmer.mrepo

import android.app.Application
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.Repository
import com.sanmer.mrepo.utils.MediaStoreUtils
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.SPUtils
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

        SPUtils.init(this)
        MediaStoreUtils.init(this)
        NotificationUtils.init(this)

        Constant.init(this)
        Repository.init(this)
        Works.init(this)
    }

    companion object {
        /**
         * Only for NotificationUtils
         */
        private lateinit var app: App
        val context get() = app
    }
}