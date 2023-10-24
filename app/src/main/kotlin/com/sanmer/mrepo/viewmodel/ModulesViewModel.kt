package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.datastore.modules.ModulesMenuExt
import com.sanmer.mrepo.datastore.repository.Option
import com.sanmer.mrepo.model.json.MagiskUpdateJson
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.model.state.LocalState
import com.sanmer.mrepo.model.state.LocalState.Companion.createState
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val suRepository: SuRepository
) : BaseModuleViewModel() {
    private val fs get() = when {
        suRepository.isInitialized -> suRepository.fs
        else -> FileSystemManager.getLocal()
    }

    private val modulesMenu get() = userPreferencesRepository.data
        .map { it.modulesMenu }

    var isSearch by mutableStateOf(false)
    private val keyFlow = MutableStateFlow("")

    private val valuesFlow = MutableStateFlow(
        listOf<Pair<LocalState, LocalModule>>()
    )
    val local get() = valuesFlow.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        isRefreshing = true
        callback()
        isRefreshing = false
    }

    private val updateJsonSaved = mutableStateMapOf<String, MagiskUpdateJson?>()

    init {
        Timber.d("ModulesViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        combine(
            localRepository.getLocalAllAsFlow().onStart { isLoading = true },
            modulesMenu,
            keyFlow,
        ) { list, menu, key ->
            if (list.isEmpty()) return@combine

            Timber.d("local list, size = ${list.size}")

            valuesFlow.value  = list.map {
                it.createState(
                    fs = fs
                ) to it
            }.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { v ->
                if (menu.pinEnabled) {
                    v.sortedByDescending { it.second.state == State.ENABLE }
                } else {
                    v
                }
            }.filter { (_, m) ->
                if (key.isBlank()) return@filter true
                key.lowercase() in "${m.name},${m.author},${m.description}".lowercase()

            }.toMutableStateList()

            if (isLoading) isLoading = false

        }.launchIn(viewModelScope)
    }

    private fun comparator(
        option: Option,
        descending: Boolean
    ): Comparator<Pair<LocalState, LocalModule>> = if (descending) {
        when (option) {
            Option.NAME -> compareByDescending { it.second.name.lowercase() }
            Option.UPDATED_TIME -> compareBy { it.first.lastUpdated }
            else -> compareByDescending { null }
        }

    } else {
        when (option) {
            Option.NAME -> compareBy { it.second.name.lowercase() }
            Option.UPDATED_TIME -> compareByDescending { it.first.lastUpdated }
            else -> compareByDescending { null }
        }
    }

    fun search(key: String) {
        keyFlow.value = key
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.value = ""
    }

    fun getLocalAll() = viewModelScope.launch {
        refreshing {
            modulesRepository.getLocalAll()
        }
    }

    fun setModulesMenu(value: ModulesMenuExt) =
        userPreferencesRepository.setModulesMenu(value)

    private fun createUiState(module: LocalModule) = when (module.state) {
        State.ENABLE -> LocalUiState(
            alpha = 1f,
            decoration = TextDecoration.None,
            toggle = { suRepository.disable(module) },
            change = { suRepository.remove(module) }
        )

        State.DISABLE -> LocalUiState(
            alpha = 0.5f,
            toggle = { suRepository.enable(module) },
            change = { suRepository.remove(module) }
        )

        State.REMOVE -> LocalUiState(
            alpha = 0.5f,
            decoration = TextDecoration.LineThrough,
            change = { suRepository.enable(module) }
        )
        State.ZYGISK_UNLOADED,
        State.RIRU_DISABLE,
        State.ZYGISK_DISABLE -> LocalUiState(
            alpha = 0.5f
        )
        State.UPDATE -> LocalUiState()
    }

    @Composable
    fun rememberUiState(module: LocalModule): LocalUiState {
        return remember(key1 = module.state, key2 = isRefreshing) {
            createUiState(module)
        }
    }

    @Composable
    fun rememberUpdateJson(module: LocalModule): MagiskUpdateJson? {
        LaunchedEffect(key1 = module) {
            if (module.ignoreUpdates) {
                updateJsonSaved.remove(module.id)
                return@LaunchedEffect
            }

            if (updateJsonSaved.containsKey(module.id)) return@LaunchedEffect

            val updateJson = if (module.updateJson.isNotBlank()) {
                MagiskUpdateJson.load(module.updateJson)
            } else {
                localRepository.getVersionById(module.id)
                    .firstOrNull()?.let {
                        MagiskUpdateJson(it)
                    }
            }

            updateJsonSaved[module.id] = updateJson
        }

        return remember {
            derivedStateOf {
                updateJsonSaved[module.id]
            }
        }.value
    }

    companion object {
        @Stable
        data class LocalUiState(
            val alpha: Float = 1f,
            val decoration: TextDecoration = TextDecoration.None,
            val toggle: (Boolean) -> Unit = {},
            val change: () -> Unit = {}
        )
    }
}