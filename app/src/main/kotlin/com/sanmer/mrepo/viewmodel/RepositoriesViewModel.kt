package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository
) : ViewModel() {
    val list = localRepository.getRepoAllAsFlow()
        .map { list ->
            list.sortedBy { it.name }
                .toMutableStateList()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = mutableStateListOf()
        )

    var progress by mutableStateOf(false)
        private set

    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        progress  = true
        callback()
        progress = false
    }

    init {
        Timber.d("RepositoriesViewModel init")
    }

    fun insert(
        repo: Repo,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        refreshing {
            modulesRepository.getRepo(repo)
                .onFailure(onFailure)
        }
    }

    fun update(repo: Repo) = viewModelScope.launch {
        localRepository.updateRepo(repo)
    }

    fun delete(repo: Repo) = viewModelScope.launch {
        localRepository.deleteRepo(repo)
        localRepository.deleteOnlineByUrl(repo.url)
    }

    fun getUpdate(
        repo: Repo,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        refreshing {
            modulesRepository.getRepo(repo)
                .onFailure(onFailure)
        }
    }

    fun getRepoAll() = viewModelScope.launch {
        refreshing {
            modulesRepository.getRepoAll(onlyEnable = false)
        }
    }
}