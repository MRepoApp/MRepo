package com.sanmer.mrepo.utils.timber

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.DebugTree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return when(priority) {
            Log.INFO -> true
            Log.DEBUG -> true
            Log.WARN -> true
            Log.ERROR -> true
            else -> false
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (!isLoggable(tag, priority)) {
            return
        }

        super.log(priority, "MRepo", message, t)
    }
}