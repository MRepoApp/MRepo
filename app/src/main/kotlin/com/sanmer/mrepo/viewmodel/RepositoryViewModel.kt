package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.datastore.repository.Option
import com.sanmer.mrepo.datastore.repository.RepositoryMenuExt
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.state.OnlineState
import com.sanmer.mrepo.model.state.OnlineState.Companion.createState
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

    private val valuesFlow = MutableStateFlow(
        listOf<Pair<OnlineState, OnlineModule>>()
    )
    val online get() = valuesFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        isRefreshing  = true
        callback()
        isRefreshing = false
    }

    init {
        Timber.d("RepositoryViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        combine(
            localRepository.getOnlineAllAsFlow(),
            repositoryMenu,
            keyFlow,
        ) { list, menu, key ->

            Timber.d("online list, size = ${list.size}")

            valuesFlow.value = list.map {
                it.createState(
                    local = localRepository.getLocalByIdOrNull(it.id)
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
            }.filter { (_, m) ->
                if (key.isBlank()) return@filter true
                key.lowercase() in (m.name + m.author + m.description).lowercase()

            }.toMutableStateList()

            isLoading = false

        }.launchIn(viewModelScope)
    }

    private fun comparator(
        option: Option,
        descending: Boolean
    ): Comparator<Pair<OnlineState, OnlineModule>> = if (descending) {
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

    fun openSearch() {
        isSearch = true
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.value = ""
    }

    fun getOnlineAll() = viewModelScope.launch {
        refreshing {
            modulesRepository.getRepoAll()
        }
    }

    fun setRepositoryMenu(value: RepositoryMenuExt) =
        userPreferencesRepository.setRepositoryMenu(value)
}