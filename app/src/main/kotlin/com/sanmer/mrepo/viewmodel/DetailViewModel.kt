package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.data.json.UpdateItem
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.provider.repo.RepoProvider
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String? = savedStateHandle["id"]
    var module = OnlineModule()
        private set

    val state = object : Status.State(initialState = Event.LOADING) {
        override fun setFailed(value: Any?) {
            super.setFailed(value)
            value?.let { message = value.toString() }
        }
    }

    val versions = mutableListOf<UpdateItem>()

    val hasChangelog get() = versions.any { it.changelog.isNotBlank() }
    var changelog: String? by mutableStateOf(null)
        private set

    val isMulti get() = module.repoId.size >= 2
    val hasLicense get() = module.license.isNotBlank()
    val hasLabel get() = hasLicense || isMulti

    var message: String? = null

    init {
        Timber.d("DetailViewModel init: $id")
        getModule()
    }

    private fun getModule() = viewModelScope.launch(Dispatchers.Default) {
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
        RepoProvider.getUpdate(module).onSuccess { list ->
            list.filterNotNull()
                .sortedByDescending { it.timestamp }
                .forEach { update ->
                    update.versions.forEach { item ->
                        val versionCodes = versions.map { it.versionCode }
                        if (item.versionCode !in versionCodes) {
                            val new = item.copy(repoId = update.repoId)
                            versions.update(new)
                        }
                    }
                }

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

    fun getChangelog(versionCode: Int) {
        val updateItem = versions.find {
            it.versionCode == versionCode
        } ?: versions.first()

        if (updateItem.changelog.isNotBlank()) {
            HttpUtils.request(
                url = updateItem.changelog,
                onSucceeded = {
                    changelog = it.string()
                },
                onFailed = {
                    changelog = it
                    Timber.d("getChangelog: $it")
                }
            )
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

    val UpdateItem.path get() = Const.DOWNLOAD_PATH.resolve(
        "${module.name}_${version}_${versionCode}.zip"
            .replace(" ", "_")
            .replace("/", "_")
    )

    fun downloader(
        context: Context,
        item: UpdateItem
    ) = DownloadService.start(
        context = context,
        module = Module(
            name = module.name,
            path = item.path.absolutePath,
            url = item.zipUrl
        ),
        install = false
    )

    fun installer(
        context: Context,
        item: UpdateItem
    ) = DownloadService.start(
        context = context,
        module = Module(
            name = module.name,
            path = item.path.absolutePath,
            url = item.zipUrl
        ),
        install = true
    )

    val OnlineModule.path get() = Const.DOWNLOAD_PATH.resolve(
        "${name}_${version}_${versionCode}.zip"
            .replace(" ", "_")
            .replace("/", "_")
    )

    fun installer(
        context: Context
    ) = DownloadService.start(
        context = context,
        module = Module(
            name = module.name,
            path = module.path.absolutePath,
            url = module.states.zipUrl
        ),
        install = true
    )
}