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

    /*
    data class UiState(
        val alpha: Float = 1f,
        val decoration: TextDecoration = TextDecoration.None,
        val toggle: (Boolean) -> Unit = {},
        val change: () -> Unit = {},
    )

    fun updateUiState(module: LocalModule): UiState {
        var alpha = 1f
        var decoration = TextDecoration.None
        var toggle: (Boolean) -> Unit = {}
        var change = {}

        when (module.state) {
            State.ENABLE -> {
                toggle = { module.disable() }
                change = { module.remove() }
            }
            State.DISABLE -> {
                alpha = 0.5f
                toggle = { module.enable() }
                change = { module.remove() }
            }
            State.REMOVE -> {
                alpha = 0.5f
                decoration = TextDecoration.LineThrough
                change = { module.enable() }
            }
            State.ZYGISK_UNLOADED,
            State.RIRU_DISABLE,
            State.ZYGISK_DISABLE -> {
                alpha = 0.5f
            }
            State.UPDATE -> {}
        }

        return UiState(alpha, decoration, toggle, change)
    }

     */
}