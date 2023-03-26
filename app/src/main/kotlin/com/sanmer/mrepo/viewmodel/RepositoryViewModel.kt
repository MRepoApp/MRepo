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
import kotlinx.coroutines.launch
import timber.log.Timber

class RepositoryViewModel : ViewModel() {
    val all = mutableStateListOf<Repo>()
    val enabled get() = all.filter { it.enable }

    var progress by mutableStateOf(false)
        private set

    init {
        Timber.d("RepositoryViewModel init")
        getAll()
    }

    fun getAll() = viewModelScope.launch {
        progress  = true
        if (all.isNotEmpty()) {
            all.clear()
        }

        val list = RepoManger.getRepoAll()
        all.addAll(list)
        progress = false
    }

    fun insert(
        repoUrl: String,
        onFailure: (Repo, Throwable) -> Unit
    ) = viewModelScope.launch {
        val repo = Repo(url = repoUrl)
        all.add(repo)
        RepoManger.insertRepo(repo)

        RepoProvider.getRepo(repo = repo)
            .onSuccess {
                val new = repo.copy(
                    name = it.name,
                    size = it.modules.size,
                    timestamp = it.timestamp
                )
                all.update(new)
            }.onFailure {
                onFailure(repo, it)
            }
    }

    fun update(repo: Repo) = viewModelScope.launch {
        all.update(repo)
        RepoManger.updateRepo(repo)
    }

    fun delete(repo: Repo) = viewModelScope.launch {
        all.remove(repo)
        RepoManger.deleteRepo(repo)
        RepoManger.deleteModules(repo.url)
    }

    fun getUpdate(
        repo: Repo,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch {
        progress = true
        RepoProvider.getRepo(repo = repo)
            .onSuccess {
                progress = false
                val new = repo.copy(
                    name = it.name,
                    size = it.modules.size,
                    timestamp = it.timestamp
                )
                all.update(new)
            }
            .onFailure {
                progress = false
                onFailure(it)
            }
    }

    fun onDestroy() = viewModelScope.launch {
        RepoManger.getRepoAll()
        RepoProvider.getRepoAll()
    }
}