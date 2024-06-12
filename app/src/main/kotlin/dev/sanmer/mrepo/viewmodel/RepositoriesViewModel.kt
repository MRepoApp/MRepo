package dev.sanmer.mrepo.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.database.entity.RepoEntity.Companion.toRepo
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
    private val reposFlow = MutableStateFlow(listOf<RepoState>())
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
                reposFlow.value = list.map { RepoState(it) }
                    .sortedBy { it.name }

                isLoading = false

            }.launchIn(viewModelScope)
    }

    fun insert(
        url: String,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            refreshing {
                modulesRepository.getRepo(url.toRepo())
                    .onFailure {
                        Timber.e(it, "insert: $url")
                        onFailure(it)
                    }
            }
        }
    }

    fun insert(repo: RepoState) {
        viewModelScope.launch {
            localRepository.insertRepo(repo.toRepo())
        }
    }

    fun delete(repo: RepoState) {
        viewModelScope.launch {
            localRepository.deleteRepo(repo.toRepo())
            localRepository.deleteOnlineByUrl(repo.url)
        }
    }

    fun update(
        repo: RepoState,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            refreshing {
                modulesRepository.getRepo(repo.toRepo())
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

    @Immutable
    data class RepoState(
        val url: String,
        val name: String,
        val enable: Boolean,
        val timestamp: Float,
        val size: Int
    ) {
        constructor(repo: RepoEntity) : this(
            url = repo.url,
            name = repo.name,
            enable = repo.enable,
            timestamp = repo.metadata.timestamp,
            size = repo.metadata.size
        )

        fun toRepo() = RepoEntity(
            url = url,
            name = name,
            enable = enable,
            metadata = RepoEntity.Metadata(
                timestamp = timestamp,
                size = size
            )
        )
    }
}