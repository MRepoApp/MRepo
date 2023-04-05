package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.State
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.json.AppUpdate
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.toFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel : ViewModel() {
    val state = object : State(initial = Event.LOADING) {
        override fun setSucceeded(value: Any?) {
            super.setSucceeded(value)
            update = value as AppUpdate
        }

        override fun setFailed(value: Any?) {
            super.setFailed(value)
            val e = value as Throwable
            Timber.e(e.message)
        }
    }

    var update: AppUpdate? by mutableStateOf(null)
        private set
    val isUpdatable get() = state.isSucceeded &&
            (update?.versionCode ?: Int.MIN_VALUE) > BuildConfig.VERSION_CODE

    val suState get() = SuProvider.state
    val envState get() = EnvProvider.state

    val localCount get() = ModuleManager.local
    val onlineCount = MutableStateFlow(0)
    val allCount get() = RepoManger.all
    val enableCount get() = RepoManger.enable

    init {
        Timber.d("HomeViewModel init")
        getAppUpdate()

        RepoManger.getRepoWithModuleAsFlow().onEach { list ->
            list.filter { it.repo.enable }
                .sumOf { it.modules.size }
                .let { onlineCount.emit(it) }
        }.launchIn(viewModelScope)
    }

    private fun getAppUpdate() = viewModelScope.launch {
        Timber.d("getAppUpdate")
        HttpUtils.requestJson<AppUpdate>(Const.UPDATE_URL + "stable.json")
            .onSuccess { update ->
                HttpUtils.requestString(update.changelog).onSuccess { text ->
                    state.setSucceeded(update.copy(changelog = text))
                }.onFailure {
                    state.setSucceeded(update)
                }
            }
            .onFailure {
                state.setFailed(it)
            }
    }

    @Suppress("RegExpRedundantEscape")
    private val url get() = update!!.apkUrl.replace(
        "\\{.*?\\}".toRegex(), Build.SUPPORTED_ABIS[0]
    )

    private val path get() = Config.DOWNLOAD_PATH.toFile().resolve(
        "mrepo-${update!!.version}-${Build.SUPPORTED_ABIS[0]}.apk"
    )

    fun observeProgress(
        owner: LifecycleOwner,
        callback: (Float) -> Unit
    ) = DownloadService.observeProgress(owner) { p, v ->
        if (v.url == url) {
            callback(p)
        }
    }

    fun installer(
        context: Context
    ) = DownloadService.start(
        context = context,
        name = "${update!!.version}(${update!!.versionCode})",
        path = path.absolutePath,
        url = url,
        install = false
    )
}