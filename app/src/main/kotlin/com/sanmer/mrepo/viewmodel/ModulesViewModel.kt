package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.provider.local.ModuleUtils.disable
import com.sanmer.mrepo.provider.local.ModuleUtils.enable
import com.sanmer.mrepo.provider.local.ModuleUtils.remove
import com.sanmer.mrepo.service.DownloadService
import timber.log.Timber

class ModulesViewModel : ViewModel() {
    // MODULES DATA
    @JvmName("getUpdatableValue")
    fun getUpdatable() = if (isSearch) _updatable else updatable

    @JvmName("getLocalValue")
    fun getLocal() = if (isSearch) _local else local

    @JvmName("getOnlineValue")
    fun getOnline() = if (isSearch) _online else online

    private val updatable by derivedStateOf {
        Constant.online.filter { module ->
            module.versionCode > (Constant.local
                .find {
                    module.id == it.id
                }?.versionCode ?: Int.MAX_VALUE)
        }
    }
    private val local by derivedStateOf {
        Constant.local.filter { module ->
            module.id !in updatable.map { it.id }
        }
    }
    private val online by derivedStateOf {
        Constant.online.toList()
    }

    // SEARCH
    var isSearch by mutableStateOf(false)
    var key by mutableStateOf("")
    private val _updatable by derivedStateOf {
        updatable.filter {
            if (key.isBlank()) return@filter true
            key.uppercase() in "${it.name}${it.author}".uppercase()
        }
    }
    private val _local by derivedStateOf {
        local.filter {
            if (key.isBlank()) return@filter true
            key.uppercase() in "${it.name}${it.author}".uppercase()
        }
    }
    private val _online by derivedStateOf {
        online.filter {
            if (key.isBlank()) return@filter true
            key.uppercase() in "${it.name}${it.author}".uppercase()
        }
    }

    fun close() {
        isSearch = false
        key = ""
    }

    //LOCAL MODULE
    fun updateModuleState(
        module: LocalModule
    ): ModuleState {
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

        return ModuleState(
            alpha = alpha,
            decoration = decoration,
            onChecked = onChecked,
            onClick = onClick
        )
    }

    //ONLINE MODULE
    fun observeProgress(
        owner: LifecycleOwner,
        value: OnlineModule,
        callback: (Float) -> Unit
    ) = DownloadService.observeProgress(owner) { p, v ->
        if (v.name == value.name) {
            callback(p)
        }
    }

    val OnlineModule.path get() = Const.DOWNLOAD_PATH.resolve(
        "${name}_${version}_${versionCode}.zip"
            .replace(" ", "_")
            .replace("/", "_")
    )

    fun downloader(
        context: Context,
        module: OnlineModule,
    ) = DownloadService.start(
        context = context,
        module = Module(
            name = module.name,
            path = module.path.absolutePath,
            url = module.states.zipUrl
        ),
        install = false
    )

    fun installer(
        context: Context,
        module: OnlineModule
    ) = DownloadService.start(
        context = context,
        module = Module(
            name = module.name,
            path = module.path.absolutePath,
            url = module.states.zipUrl
        ),
        install = true
    )

    init {
        Timber.d("ModulesViewModel init")
    }

    companion object {

        data class ModuleState(
            val alpha: Float = 1f,
            val decoration: TextDecoration = TextDecoration.None,
            val onChecked: (Boolean) -> Unit = {},
            val onClick: () -> Unit = {},
        )
    }
}