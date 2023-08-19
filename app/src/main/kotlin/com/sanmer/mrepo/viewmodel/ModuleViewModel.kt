package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.TrackJson
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.model.state.LocalState
import com.sanmer.mrepo.model.state.LocalState.Companion.createState
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.service.DownloadService
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val fs get() = try {
        suRepository.fs
    } catch (e: Exception) {
        FileSystemManager.getLocal()
    }

    private val moduleId: String = checkNotNull(savedStateHandle["moduleId"])
    var online: OnlineModule by mutableStateOf(OnlineModule.example())
        private set

    val isEmptyAbout get() = online.track.homepage.isBlank()
            && online.track.source.isBlank()
            && online.track.support.isBlank()

    var local by mutableStateOf(LocalModule.example())
        private set

    private val installed get() = local.id == online.id
    val localVersionCode get() = if (installed) local.versionCode else Int.MAX_VALUE

    private var localState: LocalState? by mutableStateOf(null)
    val versions = mutableStateListOf<Pair<Repo, VersionItem>>()
    val tracks = mutableStateListOf<Pair<Repo, TrackJson>>()

    var updatableSize by mutableIntStateOf(0)
        private set

    init {
        Timber.d("ModuleViewModel init: $moduleId")

        getModule()
        getVersionsAndTracks()
    }

    private fun getModule() {
        localRepository.online.first { it.id == moduleId }.apply {
            online = this
        }

        localRepository.local.find { it.id == moduleId }?.let {
            local = it
        }
    }

    private fun getVersionsAndTracks() = viewModelScope.launch {
        online.versions.forEach {
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
            updatableSize = versions.count { it.second.versionCode > local.versionCode }
        }
    }

    fun downloader(
        context: Context,
        downloadPath: File,
        item: VersionItem,
        install: Boolean
    ) {
        val path = downloadPath.resolve(
            "${online.name}_${item.versionDisplay}.zip"
                .replace("[\\s+|/]".toRegex(), "_")
                .replace("[^a-zA-Z0-9\\-._]".toRegex(), "")
        )

        DownloadService.start(
            context = context,
            name = online.name,
            path = path.absolutePath,
            url = item.zipUrl,
            install = install
        )
    }

    @Composable
    fun rememberProgress(item: VersionItem) =
        DownloadService.rememberProgress { it.url == item.zipUrl }

    @Composable
    fun rememberLocalState(
        suState: Event
    ): LocalState? {
        LaunchedEffect(key1 = suState, key2 = local) {
            launch(Dispatchers.Default) {
                if (installed && localState == null) {
                    localState = local.createState(fs)
                }
            }
        }

        return localState
    }
}