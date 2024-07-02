package dev.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.datastore.model.Homepage
import dev.sanmer.mrepo.datastore.model.Option
import dev.sanmer.mrepo.datastore.model.RepositoryMenu
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.ModulesRepository
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import dev.sanmer.mrepo.viewmodel.RepositoryViewModel.ModuleWrapper.Companion.wrap
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
    private val modulesRepository: ModulesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val repositoryMenu get() = userPreferencesRepository.data
        .map { it.repositoryMenu }

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")

    private val cacheFlow = MutableStateFlow(listOf<ModuleWrapper>())
    private val onlineFlow = MutableStateFlow(listOf<ModuleWrapper>())
    val online get() = onlineFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set

    init {
        Timber.d("RepositoryViewModel init")
        loadRepoData()
        dataObserver()
        keyObserver()
    }

    private fun loadRepoData() {
        viewModelScope.launch {
            modulesRepository.getRepoAll()
        }
    }

    private fun dataObserver() {
        combine(
            localRepository.getOnlineAllAsFlow(),
            localRepository.getLocalAndUpdatableAllAsFlow(),
            repositoryMenu
        ) { online, local, menu ->
            cacheFlow.value = online.map {
                it.wrap(local)
            }.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { list ->
                val listNew = when {
                    menu.pinInstalled -> list.sortedByDescending { it.installed }
                    else -> list
                }

                when {
                    menu.pinUpdatable -> listNew.sortedByDescending { it.updatable }
                    else -> list
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
                .filter { module ->
                    when {
                        key.isBlank() -> true
                        else -> module.original.name.contains(key, ignoreCase = true)
                                || module.original.author.contains(key, ignoreCase = true)
                                || module.original.description.contains(key, ignoreCase = true)
                    }
                }

        }.launchIn(viewModelScope)
    }

    private fun comparator(
        option: Option,
        descending: Boolean
    ): Comparator<ModuleWrapper> = if (descending) {
        when (option) {
            Option.Name -> compareByDescending { it.original.name.lowercase() }
            Option.UpdatedTime -> compareBy { it.lastUpdated }
        }
    } else {
        when (option) {
            Option.Name -> compareBy { it.original.name.lowercase() }
            Option.UpdatedTime -> compareByDescending { it.lastUpdated }
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

    fun setRepositoryMenu(value: RepositoryMenu) {
        viewModelScope.launch {
            userPreferencesRepository.setRepositoryMenu(value)
        }
    }

    fun setHomepage() {
        viewModelScope.launch {
            userPreferencesRepository.setHomepage(Homepage.Repository)
        }
    }

    data class ModuleWrapper(
        val original: OnlineModule,
        val installed: Boolean,
        val updatable: Boolean,
        val hasLicense: Boolean,
        val lastUpdated: Long
    ) {
        companion object {

            fun OnlineModule.wrap(
                locals: List<Pair<LocalModule, Boolean>>
            ): ModuleWrapper {
                var installed = false
                var updatable = false

                locals.firstOrNull { it.first.id == id }?.let { (local, isUpdatable) ->
                    installed = local.author == author
                    updatable = (local.versionCode < versionCode) && isUpdatable
                }

                return ModuleWrapper(
                    original = this,
                    installed = installed,
                    updatable = updatable,
                    hasLicense = metadata.license.isNotBlank(),
                    lastUpdated = versions.maxBy { it.versionCode }.timestamp
                )
            }

            fun example() = ModuleWrapper(
                original = OnlineModule.example(),
                installed = true,
                updatable = false,
                hasLicense = true,
                lastUpdated = 1660640640000L
            )
        }
    }
}