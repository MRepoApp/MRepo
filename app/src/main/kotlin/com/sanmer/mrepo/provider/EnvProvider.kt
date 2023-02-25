package com.sanmer.mrepo.provider

import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.provider.api.MagiskApi
import timber.log.Timber

object EnvProvider {
    val index get() = Config.workingMode - 1
    val isSetup get() = Config.workingMode == Config.FIRST_SETUP
    val isRoot get() = Config.workingMode == Config.MODE_ROOT
    val isNonRoot get() = Config.workingMode == Config.MODE_NON_ROOT

    val version: String get() = when {
        isRoot -> MagiskApi.getVersion()
        else -> "NULL"
    }

    fun init() {
        Timber.d("EnvProvider init")
        onRoot { MagiskApi.init() }
    }

    fun onRoot(callback: () -> Unit): EnvProvider {
        if (isRoot) callback()
        return this
    }

    fun onNonRoot(callback: () -> Unit): EnvProvider {
        if (isNonRoot) callback()
        return this
    }

    fun setMode(index: Int) {
        Config.workingMode = index + 1
    }
}