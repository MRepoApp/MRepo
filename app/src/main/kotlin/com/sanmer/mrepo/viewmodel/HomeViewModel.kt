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
import com.sanmer.mrepo.app.*
import com.sanmer.mrepo.model.json.AppUpdate
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.toFile
import com.sanmer.mrepo.works.Works
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository,
    private val works: Works
) : ViewModel() {

    var update by mutableStateOf(AppUpdate.empty())
        private set
    val isUpdatable get() = state.isSucceeded && update.versionCode > BuildConfig.VERSION_CODE

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

    val suState get() = suRepository.state
    val apiVersion get() = suRepository.version
    val enforce get() = suRepository.enforce

    val fs get() = suRepository.fs

    val localCount get() = localRepository.localCount
    val onlineCount get() = localRepository.onlineCount
    val allCount get() = localRepository.repoCount
    val enableCount get() = localRepository.enableCount

    init {
        Timber.d("HomeViewModel init")
        getAppUpdate()

        suRepository.state.onEach {
            if (it.isSucceeded) {
                works.start()
            }
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
    private val url get() = update.apkUrl.replace(
        "\\{.*?\\}".toRegex(), Build.SUPPORTED_ABIS[0]
    )

    private val path get() = Config.downloadPath.toFile().resolve(
        "app-${update.version}-${Build.SUPPORTED_ABIS[0]}-release.apk"
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
        name = "${update.version}(${update.versionCode})",
        path = path.absolutePath,
        url = url,
        install = true
    )
}