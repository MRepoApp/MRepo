package com.sanmer.mrepo

import android.app.Application
import android.content.Context
import android.content.Intent
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
        private lateinit var app: App

        /** Used in [NotificationUtils] */
        val context get() = app

        fun Context.openUrl(url: String) {
            Intent.parseUri(url, Intent.URI_INTENT_SCHEME).apply {
                startActivity(this)
            }
        }
    }
}