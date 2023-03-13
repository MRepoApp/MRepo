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
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.json.AppUpdate
import com.sanmer.mrepo.provider.app.AppProvider
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.expansion.toFile
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel : ViewModel() {
    val state = object : Status.State(Event.LOADING) {
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
    var changelog: String by mutableStateOf("")
        private set
    val isUpdatable get() = state.isSucceeded &&
            (update?.versionCode ?: Int.MIN_VALUE) > BuildConfig.VERSION_CODE

    init {
        Timber.d("HomeViewModel init")
        getAppUpdate()
    }

    private fun getAppUpdate() = viewModelScope.launch {
        Timber.d("getAppUpdate")
        AppProvider.getStable()
            .onSuccess {
                HttpUtils.requestString(it.changelog)
                    .onSuccess { text ->
                        changelog = text
                    }.onFailure {
                        changelog = ""
                    }

                state.setSucceeded(it)
            }.onFailure {
                state.setFailed(it)
            }
    }

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