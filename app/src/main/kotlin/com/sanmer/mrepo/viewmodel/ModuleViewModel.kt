package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.Compat
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.Repo.Companion.toRepo
import com.sanmer.mrepo.model.json.UpdateJson
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.TrackJson
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.ui.navigation.graphs.RepositoryScreen
import com.sanmer.mrepo.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val isEmptyAbout get() = online.track.homepage.isBlank()
            && online.track.source.isBlank()
            && online.track.support.isBlank()

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

    val versions = mutableStateListOf<Pair<Repo, VersionItem>>()
    val tracks = mutableStateListOf<Pair<Repo, TrackJson>>()

    init {
        Timber.d("ModuleViewModel init: $moduleId")
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        localRepository.getOnlineAllById(moduleId).first().let {
            online = it
        }

        localRepository.getLocalByIdOrNull(moduleId)?.let {
            local = it
            notifyUpdates = localRepository.hasUpdatableTag(moduleId)
        }

        localRepository.getVersionById(moduleId).forEach {
            val repo = localRepository.getRepoByUrl(it.repoUrl)

            val item = repo to it
            val track =  repo to localRepository.getOnlineByIdAndUrl(
                id = online.id,
                repoUrl = it.repoUrl
            ).track

            versions.add(item)
            if (track !in tracks) tracks.add(track)
        }

        if (installed) {
            UpdateJson.loadToVersionItem(local!!.updateJson)?.let {
                versions.add(0, "Update Json".toRepo() to it)
            }
        }
    }

    fun setUpdatesTag(updatable: Boolean) {
        viewModelScope.launch {
            notifyUpdates = updatable
            localRepository.insertUpdatableTag(moduleId, updatable)
        }
    }

    fun downloader(
        context: Context,
        item: VersionItem,
        onSuccess: (File) -> Unit
    ) {
        viewModelScope.launch {
            val downloadPath = userPreferencesRepository.data
                .first().downloadPath

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
                desc = item.versionDisplay
            )

            val listener = object : DownloadService.IDownloadListener {
                override fun getProgress(value: Float) {}
                override fun onSuccess() {
                    onSuccess(downloadPath.resolve(filename))
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

    @Composable
    fun getProgress(item: VersionItem): Float {
        val progress by DownloadService.getProgressByKey(item.toString())
            .collectAsStateWithLifecycle(initialValue = 0f)

        return progress
    }

    companion object {
        fun putModuleId(module: OnlineModule) =
            RepositoryScreen.View.route.replace(
                "{moduleId}", module.id
            )

        fun getModuleId(savedStateHandle: SavedStateHandle): String =
            checkNotNull(savedStateHandle["moduleId"])
    }
}