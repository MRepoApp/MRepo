package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.TrackJson
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.model.state.LocalState
import com.sanmer.mrepo.model.state.LocalState.Companion.createState
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.ui.navigation.graphs.RepositoryScreen
import com.sanmer.mrepo.works.DownloadWork
import com.sanmer.mrepo.works.DownloadWork.Companion.progressOrZero
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val moduleId = getModuleId(savedStateHandle)
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

    private val progressFlow = MutableStateFlow("url" to 0f)

    init {
        Timber.d("ModuleViewModel init: $moduleId")
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        online = localRepository.getOnlineAllById(moduleId).first()

        localRepository.getLocalByIdOrNull(moduleId)?.let { local = it }

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
            updatableSize = versions.count { it.second.versionCode > local.versionCode }
        }
    }

    private fun VersionItem.filename() = "${online.name}_${versionDisplay}.zip"
        .replace("[\\s+|/]".toRegex(), "_")

    fun downloader(
        context: Context,
        item: VersionItem,
        onSuccess: (String) -> Unit
    ) {
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork(
            item.zipUrl,
            ExistingWorkPolicy.KEEP,
            DownloadWork.start(url = item.zipUrl, filename = item.filename()),
        )

        workManager.getWorkInfosForUniqueWorkLiveData(item.zipUrl)
            .asFlow()
            .onEach { list ->
                if (list.isEmpty()) return@onEach

                val progress = list.first().progress.progressOrZero
                progressFlow.value = item.zipUrl to progress

                if (list.first().state == WorkInfo.State.SUCCEEDED) {
                    onSuccess(item.filename())
                }
            }
            .launchIn(viewModelScope)
    }

    suspend fun saveZipFile(
        context: Context,
        zip: File,
        uri: Uri
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            cr.openOutputStream(uri)?.use { output ->
                zip.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }.onSuccess {
            zip.delete()
        }.onFailure {
            Timber.d(it)
        }
    }

    @Composable
    fun rememberProgress(item: VersionItem): Float {
        val progress by progressFlow.collectAsStateWithLifecycle()
        var value by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(progress) {
            if (progress.first == item.zipUrl) {
                value = progress.second
            }
        }

        return value
    }

    @Composable
    fun rememberLocalState(suState: Event): LocalState? {

        LaunchedEffect(key1 = suState, key2 = local) {
            launch(Dispatchers.Default) {
                if (installed && localState == null) {
                    localState = local.createState(fs)
                }
            }
        }

        return localState
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