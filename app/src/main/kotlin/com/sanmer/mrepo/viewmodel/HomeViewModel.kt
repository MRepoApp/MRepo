package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.State
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.model.json.AppUpdate
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.toFile
import com.sanmer.mrepo.works.Works
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
            update = value as AppUpdate
            super.setSucceeded(value)
        }
    }

    val suState get() = suRepository.state
    val apiVersion get() = suRepository.version
    val enforce get() = suRepository.enforce
    val count get() = localRepository.count

    val progress get() = DownloadService.progress.map {
        if (it.second?.url == update.apkUrl){
            it.first
        } else {
            0f
        }
    }

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
        HttpUtils.requestJson<AppUpdate>(Const.UPDATE_URL.format("stable"))
            .onSuccess {
                val update = it.copy(
                    apkUrl = it.apkUrl.format(Build.SUPPORTED_ABIS[0])
                )

                HttpUtils.requestString(update.changelog).onSuccess { text ->
                    state.setSucceeded(update.copy(changelog = text))
                }.onFailure {
                    state.setSucceeded(update)
                }
            }.onFailure {
                state.setFailed(it)
                Timber.e(it, "getAppUpdate")
            }
    }

    fun installer(context: Context) {
        val name = "MRepo-${update.version}(${update.versionCode})"
        val path = Config.downloadPath.toFile().resolve("${name}.apk")

        DownloadService.start(
            context = context,
            name = name,
            path = path,
            url = update.apkUrl,
            install = true
        )
    }
}