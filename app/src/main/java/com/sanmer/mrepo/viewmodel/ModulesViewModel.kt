package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.OnlineModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.module.ModuleUtils.disable
import com.sanmer.mrepo.utils.module.ModuleUtils.enable
import com.sanmer.mrepo.utils.module.ModuleUtils.remove
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class ModulesViewModel : ViewModel() {

    // MODULES_DATA
    val updatable = mutableStateListOf<OnlineModule>()
    val local = mutableStateListOf<LocalModule>()
    val online = mutableStateListOf<OnlineModule>()

    private var updatableLast = listOf<OnlineModule>()
    var isUpdatable = MutableLiveData<Boolean?>(null)
        private set

    fun update() {
        clear()

        updatable.addAll(Constant.online.filter { item ->
            item.versionCode > (Constant.local
                .firstOrNull {
                    item.id == it.id
                }?.versionCode ?: Int.MAX_VALUE)
        })

        local.addAll(Constant.local.filter { item ->
            item.id !in updatable.map { it.id }
        })

        online.addAll(Constant.online.filter { item ->
            item.id !in Constant.local.map { it.id }
        })

        if (updatable.isNotEmpty()) {
            val now = updatable.toList()
            if (now != updatableLast) {
                updatableLast = updatable.toList()
                isUpdatable.postValue(true)
                //notifyUpdatable(context, updatable)
            } else {
                isUpdatable.postValue(false)
            }
        }

        sortBy()
    }

    fun clear() {
        updatable.clear()
        local.clear()
        online.clear()
    }

    fun sortBy() {
        updatable.sortBy { it.name }
        local.sortBy { it.name }
        online.sortBy { it.name }
    }


    //LOCAL_MODULE
    fun getModuleUIState(
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

    //ONLINE_MODULE
    fun observeProgress(
        owner: LifecycleOwner,
        value: OnlineModule,
        callback: (Float) -> Unit
    ) = DownloadService.observeProgress(owner) { p, v ->
        if (v.name == value.name) {
            callback(p)
        }
    }

    private fun getPath(module: OnlineModule) = Const.DOWNLOAD_PATH.resolve(
        "${module.name}_${module.version}.zip"
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
            path = getPath(module).absolutePath,
            url = module.states.zipUrl
        ),
        isInstall = false
    )

    fun installer(
        context: Context,
        module: OnlineModule
    ) = DownloadService.start(
        context = context,
        module = Module(
            name = module.name,
            path = getPath(module).absolutePath,
            url = module.states.zipUrl
        ),
        isInstall = true
    )

    // NOTIFICATION
    fun notifyUpdatable(
        context: Context
    ) {
        isUpdatable.postValue(null)
        val title = context.getString(R.string.notification_name_update)
        val text = when (updatable.size) {
            1 -> {
                context.getString(R.string.message_module_upgrade,
                    updatable.first().name)
            }
            else -> {
                context.getString(R.string.message_modules_upgrade,
                    updatable.first().name, updatable.size)
            }
        }

        NotificationUtils.notify(context, 1012) {
            setChannelId(Const.NOTIFICATION_ID_UPGRADE)
            setContentTitle(title)
            setContentText(text)
            setSilent(false)
        }
    }

    init {
        Timber.d("ModulesViewModel init")

        snapshotFlow { Constant.local.toList() }
            .onEach { update() }
            .launchIn(viewModelScope)
    }

    companion object {

        data class UIState(
            val alpha: Float = 1f,
            val decoration: TextDecoration = TextDecoration.None,
            val onChecked: (Boolean) -> Unit = {},
            val onClick: () -> Unit = {},
        )
    }
}