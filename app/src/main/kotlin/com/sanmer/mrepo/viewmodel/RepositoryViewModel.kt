package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.model.state.OnlineState.Companion.createState
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository
) : ViewModel() {

    var isSearch by mutableStateOf(false)
    var key by mutableStateOf("")

    private val online get() = localRepository.online
        .map { online ->
            online.createState(
                local = localRepository.local.find { it.id == online.id }
            ) to online
        }.sortedBy { (_, module) ->
            module.name
        }

    private val onlineSearch by derivedStateOf {
        online.filter { (_, module) ->
            if (key.isBlank()) return@filter true
            key.uppercase() in "${module.name}${module.author}".uppercase()
        }
    }

    val onlineValue get() = (if (isSearch) onlineSearch else online)

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

    fun closeSearch() {
        isSearch = false
        key = ""
    }

    fun getOnlineAll() = viewModelScope.launch {
        refreshing {
            modulesRepository.getRepoAll()
        }
    }
}