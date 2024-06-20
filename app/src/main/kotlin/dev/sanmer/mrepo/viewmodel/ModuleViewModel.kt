package dev.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.database.entity.online.RepoEntity
import dev.sanmer.mrepo.model.json.UpdateJson
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.model.online.VersionItem
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import dev.sanmer.mrepo.service.DownloadService
import dev.sanmer.mrepo.ui.activity.InstallActivity
import dev.sanmer.mrepo.ui.navigation.graphs.RepositoryScreen
import dev.sanmer.mrepo.utils.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isProviderAlive get() = Compat.isAlive

    private val moduleId = getModuleId(savedStateHandle)
    var online: OnlineModule by mutableStateOf(OnlineModule.example())
        private set
    val lastVersionItem by derivedStateOf {
        versions.firstOrNull()?.second
    }

    val isEmptyAbout get() = online.metadata.homepage.isBlank()
            && online.metadata.source.isBlank()
            && online.metadata.support.isBlank()

    var local: LocalModule? by mutableStateOf(null)
        private set

    private val installed get() = local?.let { it.author == online.author } ?: false
    var notifyUpdates by mutableStateOf(false)
        private set

    val localVersionCode get() =
        if (notifyUpdates && installed) local!!.versionCode else Int.MAX_VALUE
    val updatableSize by derivedStateOf {
        versions.count { it.second.versionCode > localVersionCode }
    }

    val versions = mutableStateListOf<Pair<RepoEntity, VersionItem>>()

    init {
        Timber.d("ModuleViewModel init: $moduleId")
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        online = localRepository.getOnlineById(moduleId).maxBy { it.versionCode }

        localRepository.getLocalAndUpdatableById(moduleId)?.let { (module, update) ->
            local = module
            notifyUpdates = update
        }

        versions.addAll(
            localRepository.getVersionAndRepoById(moduleId).flatMap { entry ->
                entry.value.map { entry.key to it }
            }.sortedByDescending { it.second.versionCode }
        )

        if (installed) {
            UpdateJson.load(local!!.updateJson)?.let {
                versions.add(0, RepoEntity("Update Json") to it)
            }
        }
    }

    fun setUpdatesTag(updatable: Boolean) {
        viewModelScope.launch {
            notifyUpdates = updatable
            localRepository.insertUpdatable(moduleId, updatable)
        }
    }

    fun downloader(
        context: Context,
        item: VersionItem,
        install: Boolean
    ) {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.data.first()
            val downloadPath = userPreferences.downloadPath

            val filename = Utils.getFilename(
                name = online.name,
                version = item.version,
                versionCode = item.versionCode,
                extension = "zip"
            )

            val task = DownloadService.TaskItem(
                key = item.toString(),
                url = item.zipUrl,
                filename = filename,
                title = online.name,
                desc = item.version
            )

            val listener = object : DownloadService.IDownloadListener {
                override fun getProgress(value: Float) {}
                override fun onSuccess() {
                    if (install) {
                        InstallActivity.start(
                            context = context,
                            file = File(downloadPath, filename)
                        )
                    }
                }

                override fun onFailure(e: Throwable) {
                    Timber.d(e)
                }
            }

            DownloadService.start(
                context = context,
                task = task,
                listener = listener
            )
        }
    }

    fun getProgress(item: VersionItem) =
        DownloadService.getProgressByKey(item.toString())

    companion object {
        fun putModuleId(module: OnlineModule) =
            RepositoryScreen.View.route.replace(
                "{moduleId}", module.id
            )

        fun getModuleId(savedStateHandle: SavedStateHandle): String =
            checkNotNull(savedStateHandle["moduleId"])
    }
}