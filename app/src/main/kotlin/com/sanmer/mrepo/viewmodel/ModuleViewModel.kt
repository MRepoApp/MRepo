package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
    private var _online: OnlineModule? by mutableStateOf(null)
    val online get() = checkNotNull(_online)
    val isEmptyAbout get() = online.track.homepage.isBlank()
            && online.track.source.isBlank()
            && online.track.support.isBlank()

    var local by mutableStateOf(LocalModule.example())
        private set

    init {
        Timber.d("ModuleViewModel init: $moduleId")

        localRepository.online.first { it.id == moduleId }.apply {
            _online = this
        }

        localRepository.local.find { it.id == moduleId }?.let {
            local = it
        }
    }

    @Composable
    fun getVersionsAndTracks(): Pair<List<Pair<Repo, VersionItem>>, List<Pair<Repo, TrackJson>>> {
        val versions = remember { mutableStateListOf<Pair<Repo, VersionItem>>() }
        val tracks = remember { mutableStateListOf<Pair<Repo, TrackJson>>() }

        LaunchedEffect(online) {
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
        }

        return versions to tracks
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
        val state: MutableState<LocalState?> = remember {
            mutableStateOf(null)
        }

        LaunchedEffect(key1 = suState, key2 = local) {
            launch(Dispatchers.Default) {
                if (local != LocalModule.example()) {
                    state.value = local.createState(fs)
                }
            }
        }

        return state.value
    }
}