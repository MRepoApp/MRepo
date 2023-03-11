package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.service.DownloadService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class ModulesViewModel : ViewModel() {
    val updatableValue get() = if (isSearch) _updatable else updatable
    val localValue get() = if (isSearch) _local else local
    val onlineValue get() = if (isSearch) _online else online

    private var local = mutableStateListOf<LocalModule>()
    private var online = mutableStateListOf<OnlineModule>()
    private val updatable by derivedStateOf {
        online.filter { module ->
            module.versionCode > (local
                .find {
                    module.id == it.id
                }?.versionCode ?: Int.MAX_VALUE)
        }
    }

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

    private var localUpdating = false
    private var onlineUpdating = false

    init {
        Timber.d("ModulesViewModel init")

        updateLocal()
        Status.Local.state.onEach {
            if (it.isSucceeded && !localUpdating) {
                updateLocal()
            }
        }.launchIn(viewModelScope)

        updateOnline()
        Status.Cloud.state.onEach {
            if (it.isSucceeded && !onlineUpdating) {
                updateOnline()
            }
        }.launchIn(viewModelScope)
    }

    private fun updateLocal() = viewModelScope.launch {
        Timber.i("ModulesViewModel: updateLocal")
        localUpdating = true

        val list = ModuleManager.getLocalAll()
        if (local.isNotEmpty()) local.clear()
        local.addAll(list)

        localUpdating = false
    }

    private fun updateOnline() = viewModelScope.launch {
        Timber.i("ModulesViewModel: updateOnline")
        onlineUpdating = true

        val list = ModuleManager.getOnlineAll()
        if (online.isNotEmpty()) online.clear()
        online.addAll(list)

        onlineUpdating = false
    }

    fun closeSearch() {
        isSearch = false
        key = ""
    }

    fun observeProgress(
        owner: LifecycleOwner,
        value: OnlineModule,
        callback: (Float) -> Unit
    ) = DownloadService.observeProgress(owner) { p, v ->
        if (v.name == value.name) {
            callback(p)
        }
    }

    private val OnlineModule.path get() = Const.DOWNLOAD_PATH.resolve(
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
}