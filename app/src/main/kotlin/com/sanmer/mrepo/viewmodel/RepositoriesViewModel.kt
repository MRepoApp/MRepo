package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toRepo
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
            initialValue = emptyList()
        )

    var progress by mutableStateOf(false)
        private set

    private inline fun <T> T.updateProgress(callback: T.() -> Unit) {
        progress  = true
        callback()
        progress = false
    }

    init {
        Timber.d("RepositoriesViewModel init")
    }

    fun insert(
        repoUrl: String,
        onFailure: (Repo, Throwable) -> Unit
    ) = viewModelScope.launch {
        updateProgress {
            val repo = repoUrl.toRepo()

            modulesRepository.getRepo(repo)
                .onSuccess {
                    localRepository.insertRepo(it)
                }.onFailure {
                    onFailure(repo, it)
                    Timber.e(it, "getRepo: $repoUrl")
                }
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
        updateProgress {
            modulesRepository.getRepo(repo)
                .onSuccess {
                    localRepository.updateRepo(it)
                }.onFailure {
                    onFailure(it)
                    Timber.e(it, "getUpdate: ${repo.url}")
                }
        }
    }
}