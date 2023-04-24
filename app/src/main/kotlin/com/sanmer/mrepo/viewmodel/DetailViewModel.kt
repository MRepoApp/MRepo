package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.State
import com.sanmer.mrepo.model.json.ModuleUpdate
import com.sanmer.mrepo.model.json.ModuleUpdateItem
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.toFile
import com.sanmer.mrepo.utils.expansion.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: String? = savedStateHandle["id"]
    var module = OnlineModule()
        private set

    val state = object : State(initial = Event.LOADING) {
        override fun setFailed(value: Any?) {
            value?.let { message = value.toString() }
            super.setFailed(value)
        }
    }

    val versions = mutableListOf<ModuleUpdateItem>()

    val hasChangelog get() = versions.any { it.changelog.isNotBlank() }
    var changelog: String? by mutableStateOf(null)
        private set

    val hasLicense get() = module.license.isNotBlank()
    val hasLabel get() = hasLicense

    var message: String? = null

    val progress get() = DownloadService.progress.map {
        if (it.second?.name == module.name){
            it.first
        } else {
            0f
        }
    }

    init {
        Timber.d("DetailViewModel init: $id")
        getModule()
    }

    private fun getModule() = viewModelScope.launch {
        if (id.isNullOrBlank()) {
            state.setFailed("The id is null or blank")
        } else {
            runCatching {
                localRepository.getOnlineAll().first { it.id == id }
            }.onSuccess {
                module = it
                getUpdates()
            }.onFailure {
                Timber.e(it, "getModule")
                state.setFailed(it.message)
            }
        }
    }

    suspend fun getRepoByUrl(url: String) = localRepository.getRepoByUrl(url)

    suspend fun getUpdates() {
        val update: (ModuleUpdate) -> Unit = { update ->
            update.versions.forEach { item ->
                val versionCodes = versions.map { it.versionCode }
                if (item.versionCode !in versionCodes) {
                    val new = item.copy(repoUrl = update.repoUrl)
                    versions.update(new)
                }
            }
        }

        val result = module.repoUrls.map { url ->
            modulesRepository.getUpdate(url, module.id)
                .onSuccess {
                    return@map Result.success(it.copy(repoUrl = url))
                }.onFailure {
                    Timber.e(it, "getUpdates")
                }
        }

        if (result.all { it.isFailure }) {
            state.setFailed(result.firstOrNull()?.exceptionOrNull())
            return
        }

        result.mapNotNull { it.getOrNull() }.let { list ->
            list.sortedByDescending { it.timestamp }
                .forEach(update)

            if (versions.isNotEmpty()) {
                versions.sortedByDescending { it.versionCode }
                state.setSucceeded()
            } else {
                state.setFailed("The versions is empty")
            }
        }
    }

    fun getChangelog(versionCode: Int) = viewModelScope.launch {
        val updateItem = versions.find {
            it.versionCode == versionCode
        } ?: versions.first()

        if (updateItem.changelog.isNotBlank()) {
            HttpUtils.requestString(
                url = updateItem.changelog
            ).onSuccess {
                changelog = it
            }.onFailure {
                changelog = it.message
                Timber.e(it, "getChangelog")
            }
        }
    }

    val ModuleUpdateItem.path get() = Config.downloadPath.toFile().resolve(
        "${module.name}_${version}_${versionCode}.zip"
            .replace("[\\s+|/]".toRegex(), "_")
    )

    fun downloader(
        context: Context,
        item: ModuleUpdateItem
    ) = DownloadService.start(
        context = context,
        name = module.name,
        path = item.path.absolutePath,
        url = item.zipUrl,
        install = false
    )

    fun installer(
        context: Context,
        item: ModuleUpdateItem
    ) = DownloadService.start(
        context = context,
        name = module.name,
        path = item.path.absolutePath,
        url = item.zipUrl,
        install = true
    )

    val OnlineModule.path get() = Config.downloadPath.toFile().resolve(
        "${name}_${version}_${versionCode}.zip"
            .replace("[\\s+|/]".toRegex(), "_")
    )

    fun installer(
        context: Context
    ) = DownloadService.start(
        context = context,
        name = module.name,
        path = module.path.absolutePath,
        url = module.states.zipUrl,
        install = true
    )
}