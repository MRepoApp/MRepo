package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.State
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.json.ModuleUpdate
import com.sanmer.mrepo.data.json.ModuleUpdateItem
import com.sanmer.mrepo.data.module.OnlineModule
import com.sanmer.mrepo.provider.repo.RepoProvider
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.toFile
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String? = savedStateHandle["id"]
    var module = OnlineModule()
        private set

    val state = object : State(initial = Event.LOADING) {
        override fun setFailed(value: Any?) {
            super.setFailed(value)
            value?.let { message = value.toString() }
        }
    }

    val versions = mutableListOf<ModuleUpdateItem>()

    val hasChangelog get() = versions.any { it.changelog.isNotBlank() }
    var changelog: String? by mutableStateOf(null)
        private set

    val hasLicense get() = module.license.isNotBlank()
    val hasLabel get() = hasLicense

    var message: String? = null

    init {
        Timber.d("DetailViewModel init: $id")
        getModule()
    }

    private fun getModule() = viewModelScope.launch {
        if (id.isNullOrBlank()) {
            state.setFailed("The id is null or blank")
        } else {
            runCatching {
                ModuleManager.getOnlineAll().first { it.id == id }
            }.onSuccess {
                module = it
                getUpdates()
            }.onFailure {
                Timber.d("getModule: ${it.message}")
                state.setFailed(it.message)
            }
        }
    }

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

        RepoProvider.getUpdate(module).onSuccess { list ->
            list.sortedByDescending { it.timestamp }
                .forEach(update)

            if (versions.isNotEmpty()) {
                versions.sortedByDescending { it.versionCode }
                state.setSucceeded()
            } else {
                state.setFailed()
            }
        }.onFailure {
            Timber.d("getUpdates: ${it.message}")
            state.setFailed(it.message)
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
                Timber.d("getChangelog: ${it.message}")
            }
        }
    }

    fun observeProgress(
        owner: LifecycleOwner,
        callback: (Float) -> Unit
    ) = DownloadService.observeProgress(owner) { p, v ->
        if (v.name == module.name) {
            callback(p)
        }
    }

    val ModuleUpdateItem.path get() = Config.DOWNLOAD_PATH.toFile().resolve(
        "${module.name}_${version}_${versionCode}.zip"
            .replace(" ", "_")
            .replace("/", "_")
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

    val OnlineModule.path get() = Config.DOWNLOAD_PATH.toFile().resolve(
        "${name}_${version}_${versionCode}.zip"
            .replace(" ", "_")
            .replace("/", "_")
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