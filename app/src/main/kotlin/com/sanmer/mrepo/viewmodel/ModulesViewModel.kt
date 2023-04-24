package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.model.module.State
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.expansion.toFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val suRepository: SuRepository
) : ViewModel() {

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

    val suState get() = suRepository.state

    var progress by mutableStateOf(false)
        private set
    private inline fun <T> T.updateProgress(callback: T.() -> Unit) {
        progress  = true
        callback()
        progress = false
    }

    init {
        Timber.d("ModulesViewModel init")

        localRepository.getLocalAllAsFlow().onEach { list ->
            if (list.isEmpty()) return@onEach

            if (local.isNotEmpty()) local.clear()
            local.addAll(list)
            Timber.i("ModulesViewModel: updateLocal")

        }.launchIn(viewModelScope)

        localRepository.getOnlineAllAsFlow().onEach { list ->
            if (list.isEmpty()) return@onEach

            if (online.isNotEmpty()) online.clear()
            online.addAll(list)
            Timber.i("ModulesViewModel: updateOnline")

        }.launchIn(viewModelScope)
    }

    fun closeSearch() {
        isSearch = false
        key = ""
    }

    fun getProgress(
        value: OnlineModule,
    ) = DownloadService.progress.map {
        if (it.second?.name == value.name){
            it.first
        } else {
            0f
        }
    }

    private val OnlineModule.path get() = Config.downloadPath.toFile().resolve(
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

    fun getRepoByUrl(
        url: String,
        callback: (Repo) -> Unit
    ) = viewModelScope.launch {
        val repo = localRepository.getRepoByUrl(url)
        callback(repo)
    }

    fun getLocalAll() = viewModelScope.launch {
        updateProgress {
            modulesRepository.getLocalAll()
        }
    }

    fun getOnlineAll() = viewModelScope.launch {
        updateProgress {
            modulesRepository.getRepoAll()
        }
    }

    @Stable
    data class LocalModuleState(
        val alpha: Float = 1f,
        val decoration: TextDecoration = TextDecoration.None,
        val toggle: (Boolean) -> Unit = {},
        val change: () -> Unit = {},
    )

    private fun createLocalModuleState(module: LocalModule): LocalModuleState {
        var alpha = 1f
        var decoration = TextDecoration.None
        var toggle: (Boolean) -> Unit = {}
        var change = {}

        when (module.state) {
            State.ENABLE -> {
                toggle = { suRepository.disable(module) }
                change = { suRepository.remove(module) }
            }
            State.DISABLE -> {
                alpha = 0.5f
                toggle = { suRepository.enable(module) }
                change = { suRepository.remove(module) }
            }
            State.REMOVE -> {
                alpha = 0.5f
                decoration = TextDecoration.LineThrough
                change = { suRepository.enable(module) }
            }
            State.ZYGISK_UNLOADED,
            State.RIRU_DISABLE,
            State.ZYGISK_DISABLE -> {
                alpha = 0.5f
            }
            State.UPDATE -> {}
        }

        return LocalModuleState(alpha, decoration, toggle, change)
    }

    @Composable
    fun rememberLocalModuleState(module: LocalModule): LocalModuleState {
        return remember(key1 = module) {
            createLocalModuleState(module)
        }
    }
}