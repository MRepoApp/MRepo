package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserDataRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.extensions.toDateTime
import com.sanmer.mrepo.utils.extensions.totalSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val userDataRepository: UserDataRepository,
    private val suRepository: SuRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val moduleId: String = checkNotNull(savedStateHandle["moduleId"])
    private var _online: OnlineModule? by mutableStateOf(null)
    val online get() = checkNotNull(_online)
    val versions get() = online.versions.sortedBy { it.versionCode }

    var local by mutableStateOf(LocalModule())
        private set
    val installed get() = local.id != "unknown"

    val userData get() = userDataRepository.userData
    val suState get() = suRepository.state

    init {
        Timber.d("ModuleViewModel init")
        getModule(moduleId)
    }

    private fun getModule(moduleId: String) = viewModelScope.launch {
        Timber.d("getModule: $moduleId")

        localRepository.online
            .first {
                it.id == moduleId
            }.apply {
                _online = this
            }

        localRepository.local
            .find {
                it.id == moduleId
            }?.let {
                local = it
            }
    }

    fun downloader(
        context: Context,
        item: VersionItem,
        install: Boolean = false
    ) {
        val path = userDataRepository.value.downloadPath.resolve(
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
    fun getRepoByUrl(url: String): Repo? {
        val repo: MutableState<Repo?> = remember { mutableStateOf(null) }

        LaunchedEffect(url) {
            repo.value = localRepository.getRepoByUrl(url)
        }

        return repo.value
    }

    @Stable
    data class LocalModuleInfo(
        val modulePath: String,
        val lastModified: String?,
        val dirSize: String?
    )

    private suspend fun createLocalModuleInfo(
        module: LocalModule
    ) = withContext(Dispatchers.Default) {
        val modulePath = "${Const.MODULE_PATH}/${module.id}"

        val lastModified: String? = try {
            val moduleProp = suRepository.fs
                .getFile("$modulePath/module.prop")

            if (moduleProp.exists()) {
                moduleProp.lastModified().toDateTime()
            } else {
                null
            }

        } catch (e: Exception) {
            Timber.e(e, "getLastModified")
            null
        }


        val dirSize: String? = try {
            val path = suRepository.fs.getFile(modulePath)

            if (path.exists()) {
                path.totalSize.formatFileSize()
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "getDirSize")
            null
        }

        return@withContext LocalModuleInfo(
            modulePath = modulePath,
            lastModified = lastModified,
            dirSize = dirSize
        )
    }

    @Composable
    fun rememberLocalModuleInfo(
        suState: Event
    ): LocalModuleInfo? {
        val info: MutableState<LocalModuleInfo?> = remember {
            mutableStateOf(null)
        }

        LaunchedEffect(key1 = suState, key2 = local) {
            info.value = createLocalModuleInfo(local)
        }

        return info.value
    }

    private fun Long.formatFileSize() = if (this < 0){
        "0 B"
    } else {
        val units = listOf("B", "KB", "MB")
        val group = (log10(toDouble()) / log10(1024.0)).toInt()
        String.format("%.2f %s", this / 1024.0.pow(group.toDouble()), units[group])
    }
}