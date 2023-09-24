package com.sanmer.mrepo.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.navigation.graphs.ModulesScreen
import com.sanmer.mrepo.utils.extensions.toFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val suRepository: SuRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val zipFile = getPath(savedStateHandle)

    val console = mutableStateListOf<String>()
    var event by mutableStateOf(Event.LOADING)
        private set

    init {
        Timber.d("InstallViewModel init")
        install()
    }

    private fun send(message: String) = console.add("- $message")

    private val onSucceeded: (LocalModule) -> Unit = {
        viewModelScope.launch {
            localRepository.insertLocal(it)
            event = Event.SUCCEEDED
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
                event = Event.FAILED
            }
        )
    }

    private fun deleteBySu() = runCatching {
        suRepository.fs.getFile(zipFile.absolutePath).delete()
    }.onFailure {
        Timber.e(it)
    }

    companion object {
        fun putPath(path: File) =
            ModulesScreen.Install.route.replace(
                "{path}", Uri.encode(path.absolutePath)
            )

        fun getPath(savedStateHandle: SavedStateHandle) =
            Uri.decode(
                checkNotNull(savedStateHandle["path"])
            ).toFile()
    }
}