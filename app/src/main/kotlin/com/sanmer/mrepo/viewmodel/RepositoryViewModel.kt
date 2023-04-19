package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository
) : ViewModel() {

    val list = mutableStateListOf<Repo>()

    var progress by mutableStateOf(false)
        private set

    private inline fun <T> T.updateProgress(callback: T.() -> Unit) {
        progress  = true
        callback()
        progress = false
    }

    init {
        Timber.d("RepositoryViewModel init")

        localRepository.getRepoAllAsFlow().onEach {
            if (it.isEmpty()) return@onEach

            if (list.isNotEmpty()) list.clear()
            list.addAll(it)

        }.launchIn(viewModelScope)
    }

    fun getAll() = viewModelScope.launch {
        updateProgress {
            val values = localRepository.getRepoAll()

            if (list.isNotEmpty()) list.clear()
            list.addAll(values)
        }
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
                }.onFailure(onFailure)
        }
    }
}