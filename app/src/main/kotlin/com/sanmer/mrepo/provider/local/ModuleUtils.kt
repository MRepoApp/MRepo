package com.sanmer.mrepo.provider.local

import android.content.Context
import androidx.compose.ui.text.style.TextDecoration
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.api.Ksu
import com.sanmer.mrepo.provider.api.Magisk
import java.io.File

object ModuleUtils {
    fun install(
        context: Context,
        onConsole: (console: String) -> Unit = {},
        onSucceeded: (LocalModule) -> Unit = {},
        onFailed: () -> Unit = {},
        onFinished: () -> Unit = {},
        zipFile: File
    ) = when {
        EnvProvider.isMagisk -> Magisk.install(context, onConsole, onSucceeded, onFailed, onFinished, zipFile)
        EnvProvider.isKsu -> Ksu.install(context, onConsole, onSucceeded, onFailed, onFinished, zipFile)
        else -> {}
    }

    private fun LocalModule.enable() = when {
        EnvProvider.isMagisk -> Magisk.enable(this)
        EnvProvider.isKsu -> Ksu.enable(this)
        else -> {}
    }

    private fun LocalModule.disable() = when {
        EnvProvider.isMagisk -> Magisk.disable(this)
        EnvProvider.isKsu -> Ksu.disable(this)
        else -> {}
    }

    private fun LocalModule.remove() = when {
        EnvProvider.isMagisk -> Magisk.remove(this)
        EnvProvider.isKsu -> Ksu.remove(this)
        else -> {}
    }

    data class UIState(
        val alpha: Float = 1f,
        val decoration: TextDecoration = TextDecoration.None,
        val onChecked: (Boolean) -> Unit = {},
        val onClick: () -> Unit = {},
    )

    fun updateUIState(
        module: LocalModule
    ): UIState {
        var alpha = 1f
        var decoration = TextDecoration.None
        var onChecked: (Boolean) -> Unit = {}
        var onClick = {}

        when (module.state) {
            State.ENABLE -> {
                onChecked = { module.disable() }
                onClick = { module.remove() }
            }
            State.DISABLE -> {
                alpha = 0.5f
                onChecked = { module.enable() }
                onClick = { module.remove() }
            }
            State.REMOVE -> {
                alpha = 0.5f
                decoration = TextDecoration.LineThrough
                onClick = { module.enable() }
            }
            State.ZYGISK_UNLOADED,
            State.RIRU_DISABLE,
            State.ZYGISK_DISABLE -> {
                alpha = 0.5f
            }
            State.UPDATE -> {}
        }

        return UIState(
            alpha = alpha,
            decoration = decoration,
            onChecked = onChecked,
            onClick = onClick
        )
    }
}