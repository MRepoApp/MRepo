package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.RepoManger.toModuleList
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.OnlineModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.provider.local.LocalProvider
import com.sanmer.mrepo.provider.local.ModuleUtils.disable
import com.sanmer.mrepo.provider.local.ModuleUtils.enable
import com.sanmer.mrepo.provider.local.ModuleUtils.remove
import com.sanmer.mrepo.provider.repo.RepoProvider
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.expansion.merge
import com.sanmer.mrepo.utils.expansion.toFile
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class ModulesViewModel : ViewModel() {
    val updatableValue get() = if (isSearch) _updatable else updatable
    val localValue get() = if (isSearch) _local else local
    val onlineValue get() = if (isSearch) _online else online

    private val local = mutableStateListOf<LocalModule>()
    private val online = mutableStateListOf<OnlineModule>()
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

    val suState get() = SuProvider.state
    var progress by mutableStateOf(false)
        private set

    private inline fun <T> T.updateProgress(callback: T.() -> Unit) {
        progress  = true
        callback()
        progress = false
    }

    init {
        Timber.d("ModulesViewModel init")

        ModuleManager.getLocalAllAsFlow().onEach { list ->
            if (list.isEmpty()) return@onEach

            if (local.isNotEmpty()) local.clear()
            local.addAll(list)
            Timber.i("ModulesViewModel: updateLocal")

        }.launchIn(viewModelScope)

        RepoManger.getRepoWithModuleAsFlow().onEach { list ->
            if (list.isEmpty()) return@onEach

            val values = list.filter { it.repo.enable }
                .map { it.modules }
                .merge()
                .toModuleList()

            if (online.isNotEmpty()) online.clear()
            online.addAll(values)
            Timber.i("ModulesViewModel: updateOnline")

        }.launchIn(viewModelScope)
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

    private val OnlineModule.path get() = Config.DOWNLOAD_PATH.toFile().resolve(
        "${name}_${version}_${versionCode}.zip"
            .replace(" ", "_")
            .replace("/", "_")
    )

    fun downloader(
        context: Context,
        module: OnlineModule,
    ) = DownloadService.start(
        context = context,
        name = module.name,
        path = module.path.absolutePath,
        url = module.states.zipUrl,
        install = false
    )

    fun installer(
        context: Context,
        module: OnlineModule
    ) = DownloadService.start(
        context = context,
        name = module.name,
        path = module.path.absolutePath,
        url = module.states.zipUrl,
        install = true
    )

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

    fun getRepoByUrl(
        url: String,
        callback: (Repo) -> Unit
    ) = viewModelScope.launch {
        val repo = RepoManger.getRepoByUrl(url)
        callback(repo)
    }

    fun getLocalAll() = viewModelScope.launch {
        updateProgress {
            LocalProvider.getLocalAll().onFailure {
                Timber.e("getLocalAll: ${it.message}")
            }
        }
    }

    fun getOnlineAll() = viewModelScope.launch {
        updateProgress {
            RepoProvider.getRepoAll().onFailure {
                Timber.e("getRepoAll: ${it.message}")
            }
        }
    }
}