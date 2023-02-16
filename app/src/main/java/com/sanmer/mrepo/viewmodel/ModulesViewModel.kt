package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.App
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.data.provider.local.ModuleUtils.disable
import com.sanmer.mrepo.data.provider.local.ModuleUtils.enable
import com.sanmer.mrepo.data.provider.local.ModuleUtils.remove
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class ModulesViewModel : ViewModel() {
    val context = App.context

    // UPDATA STATE
    private val updataState = object : Status.Events() {
        override fun setLoading(value: Any?) {
            Timber.d("ModulesViewModel updata")
            super.setLoading(value)
        }

        override fun setSucceeded(value: Any?) {
            Timber.d("updata is succeeded")
            super.setSucceeded(value)
        }

        override fun setFailed(value: Any?) {
            Timber.e("updata: $value")
            super.setFailed(value)
        }
    }

    // MODULES DATA
    fun getUpdatable() = if (isSearch) _updatable else updatable
    fun getLocal() = if (isSearch) _local else local
    fun getOnline() = if (isSearch) _online else online

    private val updatable = mutableStateListOf<OnlineModule>()
    private val local = mutableStateListOf<LocalModule>()
    private val online = mutableStateListOf<OnlineModule>()

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

    @Synchronized
    fun updata() = runCatching {
        if (updataState.isLoading) {
            Timber.w("updata is already loading!")
            return@runCatching
        } else {
            updataState.setLoading()
        }

        Constant.online.forEach { module ->
            val isUpdatable = module.versionCode > (Constant.local
                .find {
                    module.id == it.id
                }?.versionCode ?: Int.MAX_VALUE)

            if (isUpdatable) {
                updatable.update(module)
            } else {
                if (module in updatable) updatable.remove(module)
            }
        }

        local.forEach {
            if (it !in Constant.local) local.remove(it)
        }
        val updatableId = updatable.map { it.id }
        Constant.local.forEach {
            if (it.id !in updatableId) {
                local.update(it)
            }
        }

        online.forEach {
            if (it !in Constant.online) online.remove(it)
        }
        Constant.online.forEach {
            online.update(it)
        }

        if (updatable.isNotEmpty()) {
            notifyUpdatable(context = context)
        }

        updataState.setSucceeded()
        sortBy()
    }.onFailure {
        updataState.setFailed(it)
    }

    private fun sortBy() {
        updatable.sortBy { it.name }
        local.sortBy { it.name }
        online.sortBy { it.name }
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

    // NOTIFICATION
    private fun notifyUpdatable(
        context: Context
    ) {
        val title = context.getString(R.string.notification_name_update)
        val text = when (updatable.size) {
            1 -> {
                context.getString(R.string.message_module_updated,
                    updatable.first().name)
            }
            else -> {
                context.getString(R.string.message_modules_updated,
                    updatable.first().name, updatable.size)
            }
        }

        NotificationUtils.notify(context, Const.NOTIFICATION_ID_1) {
            setChannelId(Const.CHANNEL_ID_UPDATE)
            setContentIntent(NotificationUtils.getActivity(MainActivity::class))
            setContentTitle(title)
            setContentText(text)
            setSilent(false)
        }
    }

    init {
        Timber.d("ModulesViewModel init")

        snapshotFlow { Constant.cloud.toList() }
            .onEach { updata() }
            .launchIn(viewModelScope)

        snapshotFlow { Constant.local.toList() }
            .onEach { updata() }
            .launchIn(viewModelScope)
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