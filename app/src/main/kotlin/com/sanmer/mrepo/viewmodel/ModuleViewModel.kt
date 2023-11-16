package com.sanmer.mrepo.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.model.json.MagiskUpdateJson
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.TrackJson
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.model.state.LocalState
import com.sanmer.mrepo.model.state.LocalState.Companion.createState
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.ui.navigation.graphs.RepositoryScreen
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val suProvider: SuProvider,
    savedStateHandle: SavedStateHandle
) : DownloadViewModel() {
    private val fs get() = when {
        suProvider.isInitialized -> suProvider.fs
        else -> FileSystemManager.getLocal()
    }

    private val moduleId = getModuleId(savedStateHandle)
    var online: OnlineModule by mutableStateOf(OnlineModule.example())
        private set

    val isEmptyAbout get() = online.track.homepage.isBlank()
            && online.track.source.isBlank()
            && online.track.support.isBlank()

    var local by mutableStateOf(LocalModule.example())
        private set

    private val installed get() = local != LocalModule.example()
    private val notifyUpdates get() = installed && !local.ignoreUpdates
    val localVersionCode get() = if (notifyUpdates) local.versionCode else Int.MAX_VALUE

    var localState: LocalState? by mutableStateOf(null)
        private set
    val versions = mutableStateListOf<Pair<Repo, VersionItem>>()
    val tracks = mutableStateListOf<Pair<Repo, TrackJson>>()

    val updatableSize by derivedStateOf {
        if (notifyUpdates) {
            versions.count { it.second.versionCode > local.versionCode }
        } else {
            0
        }
    }

    init {
        Timber.d("ModuleViewModel init: $moduleId")
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        localRepository.getOnlineAllById(moduleId).first().let {
            online = it
            setFilePrefix(it.name)
        }

        localRepository.getLocalByIdOrNull(moduleId)?.let {
            local = it
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

        if (!installed) return@launch

        localState = local.createState(fs)

        val updateJson = MagiskUpdateJson.load(local.updateJson)
        updateJson?.toItemOrNull()?.let {
            versions.add(0, "Update Json".toRepo() to it)
        }
    }

    fun setIgnoreUpdates(ignore: Boolean) {
        viewModelScope.launch {
            local.ignoreUpdates = ignore
            localRepository.insertLocal(local)
        }
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