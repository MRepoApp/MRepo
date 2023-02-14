package com.sanmer.mrepo.provider

import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.provider.api.MagiskApi

object EnvProvider {
    val isSetup get() = Config.workingMode == Config.FIRST_SETUP
    val isRoot get() = Config.workingMode == Config.MODE_ROOT

    val version: String get() = when {
        isRoot -> MagiskApi.getVersion()
        else -> "NULL"
    }

    fun init() {
        onRoot { MagiskApi.init() }
    }

    fun onRoot(callback: () -> Unit): EnvProvider {
        if (isRoot) callback()
        return this
    }
}