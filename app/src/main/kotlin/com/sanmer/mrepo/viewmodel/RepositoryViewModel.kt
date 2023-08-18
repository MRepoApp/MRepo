package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var isSearch by mutableStateOf(false)
    var key by mutableStateOf("")

    private val online get() = localRepository.online
        .map { online ->
            online.createState(
                local = localRepository.local.find { it.id == online.id }
            ) to online
        }

    private val _online by derivedStateOf {
        online.filter { (_, module) ->
            if (key.isBlank()) return@filter true
            key.uppercase() in "${module.name}${module.author}".uppercase()
        }
    }

    private val values get() = (if (isSearch) _online else online)

    var isRefreshing by mutableStateOf(false)
        private set
    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        isRefreshing  = true
        callback()
        isRefreshing = false
    }

    init {
        Timber.d("RepositoryViewModel init")
    }

    @Composable
    fun getOnlineSortedBy(
        menu: RepositoryMenuExt
    ): List<Pair<OnlineState, OnlineModule>> {
        val list = remember(menu) {
            derivedStateOf {
                values.sortedWith(
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
            }
        }

        return list.value
    }

    private fun comparator(option: Option, descending: Boolean): Comparator<Pair<OnlineState, OnlineModule>> =
        if (descending) {
            when (option) {
                Option.NAME -> compareByDescending { it.second.name }
                Option.UPDATED_TIME -> compareBy { it.first.lastUpdated }
                else -> compareByDescending { null }
            }

        } else {
            when (option) {
                Option.NAME -> compareBy { it.second.name }
                Option.UPDATED_TIME -> compareByDescending { it.first.lastUpdated }
                else -> compareByDescending { null }
            }
        }

    fun closeSearch() {
        isSearch = false
        key = ""
    }

    fun getOnlineAll() = viewModelScope.launch {
        refreshing {
            modulesRepository.getRepoAll()
        }
    }

    fun setRepositoryMenu(value: RepositoryMenuExt) =
        userPreferencesRepository.setRepositoryMenu(value)
}