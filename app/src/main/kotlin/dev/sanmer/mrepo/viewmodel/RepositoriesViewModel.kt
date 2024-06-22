package dev.sanmer.mrepo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.database.entity.online.RepoEntity
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.ModulesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository
) : ViewModel() {
    private val reposFlow = MutableStateFlow(listOf<RepoEntity>())
    val repos get() = reposFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set
    var progress by mutableStateOf(false)
        private set
    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        progress = true
        callback()
        progress = false
    }

    init {
        Timber.d("RepositoriesViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        localRepository.getRepoAllAsFlow()
            .onEach { list ->
                reposFlow.value = list.sortedBy { it.name }

                isLoading = false

            }.launchIn(viewModelScope)
    }

    fun insert(
        url: String,
        onFailure: (Throwable) -> Unit
    ) {
        val repoUrl = if (url.endsWith("/")) url else "${url}/"
        viewModelScope.launch {
            refreshing {
                modulesRepository.getRepo(repoUrl)
                    .onFailure {
                        Timber.e(it, "insert: $url")
                        onFailure(it)
                    }
            }
        }
    }

    fun insert(repo: RepoEntity) {
        viewModelScope.launch {
            localRepository.insertRepo(repo)
        }
    }

    fun delete(repo: RepoEntity) {
        viewModelScope.launch {
            localRepository.deleteRepo(repo)
        }
    }

    fun update(
        repo: RepoEntity,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            refreshing {
                modulesRepository.getRepo(repo)
                    .onFailure {
                        Timber.e(it, "update: ${repo.url}")
                        onFailure(it)
                    }
            }
        }
    }

    fun getRepoAll() {
        viewModelScope.launch {
            refreshing {
                modulesRepository.getRepoAll(onlyEnable = false)
            }
        }
    }
}