package dev.sanmer.mrepo.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.datastore.Option
import dev.sanmer.mrepo.datastore.RepositoryMenuCompat
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val repositoryMenu get() = userPreferencesRepository.data
        .map { it.repositoryMenu }

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")

    private val cacheFlow = MutableStateFlow(listOf<Pair<OnlineState, OnlineModule>>())
    private val onlineFlow = MutableStateFlow(listOf<Pair<OnlineState, OnlineModule>>())
    val online get() = onlineFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set

    init {
        Timber.d("RepositoryViewModel init")
        dataObserver()
        keyObserver()
    }

    private fun dataObserver() {
        combine(
            localRepository.getOnlineAllAsFlow(),
            repositoryMenu
        ) { list, menu ->
            cacheFlow.value = list.map {
                it.createState(
                    local = localRepository.getLocalByIdOrNull(it.id),
                    hasUpdatableTag = localRepository.hasUpdatableTag(it.id)
                ) to it
            }.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { v ->
                val a = if (menu.pinInstalled) {
                    v.sortedByDescending { it.first.installed }
                } else {
                    v
                }

                if (menu.pinUpdatable) {
                    a.sortedByDescending { it.first.updatable }
                } else {
                    a
                }
            }

            isLoading = false

        }.launchIn(viewModelScope)
    }

    private fun keyObserver() {
        combine(
            keyFlow,
            cacheFlow
        ) { key, source ->
            onlineFlow.value = source
                .filter { (_, m) ->
                    if (key.isNotBlank()) {
                        m.name.contains(key, ignoreCase = true)
                                || m.author.contains(key, ignoreCase = true)
                                || m.description.contains(key, ignoreCase = true)
                    } else {
                        true
                    }
                }

        }.launchIn(viewModelScope)
    }

    private fun comparator(
        option: Option,
        descending: Boolean
    ): Comparator<Pair<OnlineState, OnlineModule>> = if (descending) {
        when (option) {
            Option.Name -> compareByDescending { it.second.name.lowercase() }
            Option.UpdatedTime -> compareBy { it.first.lastUpdated }
            else -> compareByDescending { null }
        }

    } else {
        when (option) {
            Option.Name -> compareBy { it.second.name.lowercase() }
            Option.UpdatedTime -> compareByDescending { it.first.lastUpdated }
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

    fun setRepositoryMenu(value: RepositoryMenuCompat) {
        viewModelScope.launch {
            userPreferencesRepository.setRepositoryMenu(value)
        }
    }

    private fun OnlineModule.createState(
        local: LocalModule?,
        hasUpdatableTag: Boolean,
    ): OnlineState {
        val installed = local != null && local.id == id
                && local.author == author

        val updatable = if (installed && hasUpdatableTag) {
            local!!.versionCode < versionCode
        } else {
            false
        }

        return OnlineState(
            installed = installed,
            updatable = updatable,
            hasLicense = track.license.isNotBlank(),
            lastUpdated = versions.firstOrNull()?.timestamp ?: 1.47333965E9f
        )
    }

    @Immutable
    data class OnlineState(
        val installed: Boolean,
        val updatable: Boolean,
        val hasLicense: Boolean,
        val lastUpdated: Float
    ) {
        companion object {
            fun example() = OnlineState(
                installed = true,
                updatable = false,
                hasLicense = true,
                lastUpdated = 1.66064064E9f
            )
        }
    }
}