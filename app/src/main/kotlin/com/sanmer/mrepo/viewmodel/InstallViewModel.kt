package com.sanmer.mrepo.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.app.utils.MediaStoreUtils.absolutePath
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.utils.extensions.toFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val path: String = checkNotNull(savedStateHandle["path"])
    private val zipFile = getPath(path).toFile()

    val console = mutableStateListOf<String>()
    val state = object : State(initial = Event.LOADING) {
        override fun setFailed(value: Any?) {
            value?.let { send(it.toString())}
            super.setFailed(value)
        }
    }

    init {
        Timber.d("InstallViewModel init")
        install()
    }

    private fun send(message: String) = console.add("- $message")

    private val onSucceeded: (LocalModule) -> Unit = {
        viewModelScope.launch {
            localRepository.insertLocal(it)
            state.setSucceeded()
        }
    }

    private fun install() = viewModelScope.launch {
        val deleteZipFile = userPreferencesRepository
            .data.first().deleteZipFile

        send("Installing ${zipFile.name}")

        suRepository.install(
            zipFile = zipFile,
            console = { console.add(it) },
            onSuccess = {
                onSucceeded(it)
                if (deleteZipFile) deleteBySu()
            },
            onFailure = {
                state.setFailed()
            }
        )
    }

    private fun deleteBySu() = runCatching {
        suRepository.fs.getFile(zipFile.absolutePath).delete()
    }.onFailure {
        Timber.e(it)
    }

    companion object {
        fun createRoute(uri: Uri) = "Install/${
            uri.absolutePath.replace("/", "@")
        }"

        fun getPath(path: String) = path.replace("@", "/")
    }
}