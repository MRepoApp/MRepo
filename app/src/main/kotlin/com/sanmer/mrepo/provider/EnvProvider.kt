package com.sanmer.mrepo.provider

import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.provider.api.KsuApi
import com.sanmer.mrepo.provider.api.MagiskApi
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

object EnvProvider {
    val state = MutableStateFlow(Event.NON)
    val event: Event get() = state.value

    val context get() = SELinux.Root.context
    val isKsu get() = context == "u:r:su:s0"
    val isMagisk get() = context == "u:r:magisk:s0"

    val index get() = Config.WORKING_MODE - 1
    val isSetup get() = Config.WORKING_MODE == Config.FIRST_SETUP
    val isRoot get() = Config.WORKING_MODE == Config.MODE_ROOT
    val isNonRoot get() = Config.WORKING_MODE == Config.MODE_NON_ROOT

    val version: String get() =  when {
        isMagisk -> MagiskApi.version
        isKsu -> KsuApi.version
        else -> ""
    }

    fun init() {
        Timber.d("EnvProvider init")

        when {
            isMagisk -> MagiskApi.init(
                onSucceeded = { state.value = Event.SUCCEEDED },
                onFailed = { state.value = Event.FAILED }
            )
            isKsu -> KsuApi.init(
                onSucceeded = { state.value = Event.SUCCEEDED },
                onFailed = { state.value = Event.FAILED }
            )
        }
    }

    fun setMode(index: Int) {
        Config.WORKING_MODE = index + 1
    }
}