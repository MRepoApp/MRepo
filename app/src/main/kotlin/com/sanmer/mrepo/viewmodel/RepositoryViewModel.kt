package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.provider.repo.RepoProvider
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class RepositoryViewModel : ViewModel() {
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

        RepoManger.getRepoAllAsFlow().onEach {
            if (it.isEmpty()) return@onEach

            if (list.isNotEmpty()) list.clear()
            list.addAll(it)

        }.launchIn(viewModelScope)
    }

    fun getAll() = viewModelScope.launch {
        updateProgress {
            val values = RepoManger.getRepoAll()

            if (list.isNotEmpty()) list.clear()
            list.addAll(values)
        }
    }

    fun insert(
        repoUrl: String,
        onFailure: (Repo, Throwable) -> Unit
    ) = viewModelScope.launch {
        val repo = Repo(url = repoUrl)
        list.add(repo)
        RepoManger.insertRepo(repo)

        RepoProvider.getRepo(repo).onSuccess {
            val new = repo.copy(
                name = it.name,
                size = it.modules.size,
                timestamp = it.timestamp
            )
            list.update(new)
        }.onFailure {
            onFailure(repo, it)
        }
    }

    fun update(repo: Repo) = viewModelScope.launch {
        list.update(repo)
        RepoManger.updateRepo(repo)
    }

    fun delete(repo: Repo) = viewModelScope.launch {
        list.remove(repo)
        RepoManger.deleteRepo(repo)
        RepoManger.deleteModules(repo.url)
    }

    fun getUpdate(
        repo: Repo,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        updateProgress {
            RepoProvider.getRepo(repo).onSuccess {
                val new = repo.copy(
                    name = it.name,
                    size = it.modules.size,
                    timestamp = it.timestamp
                )
                list.update(new)
            }.onFailure(onFailure)
        }
    }

    fun onDestroy() = viewModelScope.launch {
        RepoProvider.getRepoAll()
    }
}