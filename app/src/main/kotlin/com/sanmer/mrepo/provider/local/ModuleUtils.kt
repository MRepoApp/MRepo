package com.sanmer.mrepo.provider.local

import android.content.Context
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.api.KsuApi
import com.sanmer.mrepo.provider.api.MagiskApi
import java.io.File

object ModuleUtils {
    fun install(
        context: Context,
        onConsole: (String) -> Unit = {},
        onSucceeded: (LocalModule) -> Unit = {},
        onFailed: () -> Unit = {},
        zipFile: File
    ) = when {
        EnvProvider.isMagisk -> MagiskApi.install(context, onConsole, onSucceeded, onFailed, zipFile)
        EnvProvider.isKsu -> KsuApi.install(context, onConsole, onSucceeded, onFailed, zipFile)
        else -> {}
    }

    fun LocalModule.enable() = when {
        EnvProvider.isMagisk -> MagiskApi.enable(this)
        EnvProvider.isKsu -> KsuApi.enable(this)
        else -> {}
    }

    fun LocalModule.disable() = when {
        EnvProvider.isMagisk -> MagiskApi.disable(this)
        EnvProvider.isKsu -> KsuApi.disable(this)
        else -> {}
    }

    fun LocalModule.remove() = when {
        EnvProvider.isMagisk -> MagiskApi.remove(this)
        EnvProvider.isKsu -> KsuApi.remove(this)
        else -> {}
    }
}