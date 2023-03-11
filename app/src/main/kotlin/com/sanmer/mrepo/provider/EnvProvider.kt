package com.sanmer.mrepo.provider

import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.provider.api.Ksu
import com.sanmer.mrepo.provider.api.Magisk
import timber.log.Timber

object EnvProvider {
    val index get() = Config.WORKING_MODE - 1
    val isSetup get() = Config.WORKING_MODE == Config.FIRST_SETUP
    val isRoot get() = Config.WORKING_MODE == Config.MODE_ROOT
    val isNonRoot get() = Config.WORKING_MODE == Config.MODE_NON_ROOT

    val version: String get() =  when {
        isMagisk -> Magisk.version
        isKsu -> Ksu.version
        else -> ""
    }

    val context get() = SELinux.Root.context
    val isKsu get() = context == "u:r:su:s0"
    val isMagisk get() = context == "u:r:magisk:s0"

    fun init() {
        Timber.d("EnvProvider init")
        Status.Env.setLoading()

        when {
            isMagisk -> Magisk.init()
            isKsu -> Ksu.init()
            else -> Status.Env.setFailed()
        }
    }

    fun setMode(index: Int) {
        Config.WORKING_MODE = index + 1
    }
}