package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.model.json.ModuleUpdate
import com.sanmer.mrepo.model.json.ModuleUpdateItem
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.UserDataRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    var module = OnlineModule()
        private set
    val hasLicense get() = module.license.isNotBlank()
    val hasLabel get() = hasLicense

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
    var message: String? = null
        private set

    val progress get() = DownloadService.getProgress { it.name == module.name }
    val userData get() = userDataRepository.userData

    init {
        Timber.d("ModuleViewModel init")
        getModule(moduleId)
    }

    private fun getModule(moduleId: String) = viewModelScope.launch {
        Timber.d("getModule: $moduleId")

        runCatching {
            localRepository.online.first { it.id == moduleId }
        }.onSuccess {
            module = it
            getUpdates()
        }.onFailure {
            Timber.e(it, "getModule")
            state.setFailed(it.message)
        }
    }

    suspend fun getRepoByUrl(url: String) = localRepository.getRepoByUrl(url)

    private suspend fun getUpdates() {
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

    fun downloader(
        context: Context,
        item: ModuleUpdateItem,
        install: Boolean = false
    ) {
        val path = userDataRepository.value.downloadPath.resolve(
            "${module.name}_${item.version}_${item.versionCode}.zip"
                .replace("[\\s+|/]".toRegex(), "_")
        )

        DownloadService.start(
            context = context,
            name = module.name,
            path = path.absolutePath,
            url = item.zipUrl,
            install = install
        )
    }

    fun installer(context: Context) {
        val path = userDataRepository.value.downloadPath.resolve(
            "${module.name}_${module.version}_${module.versionCode}.zip"
                .replace("[\\s+|/]".toRegex(), "_")
        )

        DownloadService.start(
            context = context,
            name = module.name,
            path = path.absolutePath,
            url = module.states.zipUrl,
            install = true
        )
    }
}