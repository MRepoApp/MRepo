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
import com.sanmer.mrepo.model.json.UpdateJson
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.provider.ProviderCompat
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : DownloadViewModel() {
    val isProviderAlive get() = ProviderCompat.isAlive

    private val modulesMenu get() = userPreferencesRepository.data
        .map { it.modulesMenu }

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")

    private val valuesFlow = MutableStateFlow(
        listOf<LocalModule>()
    )
    val local get() = valuesFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        isRefreshing = true
        callback()
        isRefreshing = false
    }

    private val updateJsonSaved = mutableStateMapOf<String, UpdateJson?>()

    init {
        Timber.d("ModulesViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        combine(
            localRepository.getLocalAllAsFlow(),
            modulesMenu,
            keyFlow,
        ) { list, menu, key ->
            if (list.isEmpty()) return@combine

            Timber.d("local list, size = ${list.size}")

            valuesFlow.value  = list.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { v ->
                if (menu.pinEnabled) {
                    v.sortedByDescending { it.state == State.ENABLE }
                } else {
                    v
                }
            }.filter { m ->
                if (key.isBlank()) return@filter true
                key.lowercase() in (m.name + m.author + m.description).lowercase()

            }.toMutableStateList()

            isLoading = false

        }.launchIn(viewModelScope)
    }

    private fun comparator(
        option: Option,
        descending: Boolean
    ): Comparator<LocalModule> = if (descending) {
        when (option) {
            Option.NAME -> compareByDescending { it.name.lowercase() }
            Option.UPDATED_TIME -> compareBy { it.lastUpdated }
            else -> compareByDescending { null }
        }

    } else {
        when (option) {
            Option.NAME -> compareBy { it.name.lowercase() }
            Option.UPDATED_TIME -> compareByDescending { it.lastUpdated }
            else -> compareByDescending { null }
        }
    }

    fun search(key: String) {
        keyFlow.value = key
    }

    fun openSearch() {
        isSearch = true
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.value = ""
    }

    fun getLocalAll() {
        viewModelScope.launch {
            refreshing {
                modulesRepository.getLocalAll()
            }
        }
    }

    fun setModulesMenu(value: ModulesMenuExt) =
        userPreferencesRepository.setModulesMenu(value)

    private fun getLocal(id: String) {
        viewModelScope.launch {
            modulesRepository.getLocal(id)
        }
    }

    private fun createUiState(module: LocalModule) = when (module.state) {
            State.ENABLE -> LocalUiState(
                alpha = 1f,
                decoration = TextDecoration.None,
                toggle = {
                    ProviderCompat.moduleManager
                        .disable(module.id)

                    getLocal(module.id)
                },
                change = {
                    ProviderCompat.moduleManager
                        .remove(module.id)

                    getLocal(module.id)
                }
            )

            State.DISABLE -> LocalUiState(
                alpha = 0.5f,
                toggle = {
                    ProviderCompat.moduleManager
                        .enable(module.id)

                    getLocal(module.id)
                },
                change = {
                    ProviderCompat.moduleManager
                        .remove(module.id)

                    getLocal(module.id)
                }
            )

            State.REMOVE -> LocalUiState(
                alpha = 0.5f,
                decoration = TextDecoration.LineThrough,
                change = {
                    ProviderCompat.moduleManager
                        .enable(module.id)

                    getLocal(module.id)
                }
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
    fun rememberUpdateJson(module: LocalModule): UpdateJson? {
        LaunchedEffect(key1 = module) {
            if (!localRepository.hasUpdatableTag(module.id)) {
                updateJsonSaved.remove(module.id)
                return@LaunchedEffect
            }

            if (updateJsonSaved.containsKey(module.id)) return@LaunchedEffect

            val updateJson = if (module.updateJson.isNotBlank()) {
                UpdateJson.load(module.updateJson)
            } else {
                localRepository.getVersionById(module.id)
                    .firstOrNull()?.let {
                        UpdateJson(it)
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