package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.model.json.AppUpdate
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserDataRepository
import com.sanmer.mrepo.service.DownloadService
import com.sanmer.mrepo.utils.HttpUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val userDataRepository: UserDataRepository,
    private val suRepository: SuRepository
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

    val userData get() = userDataRepository.userData
    val suState get() = suRepository.state
    val apiVersion get() = suRepository.version
    val enforce get() = suRepository.enforce
    val count get() = localRepository.count

    val progress get() = DownloadService.getProgress { it.url == update.apkUrl }

    init {
        Timber.d("HomeViewModel init")
        getAppUpdate()
    }

    private fun getAppUpdate() = viewModelScope.launch {
        HttpUtils.requestJson<AppUpdate>(Const.UPDATE_URL.format("stable"))
            .onSuccess {
                HttpUtils.requestString(update.changelog)
                    .onSuccess { text ->
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
        val path = userDataRepository.downloadPath.resolve("${name}.apk")

        DownloadService.start(
            context = context,
            name = name,
            path = path,
            url = update.apkUrl,
            install = true
        )
    }

    fun setWorkingMode(value: WorkingMode) = userDataRepository.setWorkingMode(value)
    fun setDarkTheme(value: DarkMode) = userDataRepository.setDarkTheme(value)
    fun setThemeColor(value: Int) = userDataRepository.setThemeColor(value)
    fun setDownloadPath(value: String) = userDataRepository.setDownloadPath(value)
    fun setDeleteZipFile(value: Boolean) = userDataRepository.setDeleteZipFile(value)
}