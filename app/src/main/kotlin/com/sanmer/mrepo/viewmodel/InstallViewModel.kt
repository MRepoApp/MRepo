package com.sanmer.mrepo.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.provider.ProviderCompat
import com.sanmer.mrepo.provider.stub.IInstallCallback
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.navigation.graphs.ModulesScreen
import com.sanmer.mrepo.utils.extensions.now
import com.sanmer.mrepo.utils.extensions.tmpDir
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor(
    private val modulesRepository: ModulesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val context: Context by lazy { getApplication() }
    private val zipFile = getPath(savedStateHandle)

    val console = mutableStateListOf<String>()
    var event by mutableStateOf(Event.LOADING)
        private set

    val logfile get() = "module_install_log_${LocalDateTime.now()}.log"

    init {
        Timber.d("InstallViewModel init")
    }

    suspend fun saveLog(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            cr.openOutputStream(uri)?.use {
                it.write(console.joinToString(separator = "\n").toByteArray())
            }
        }.onFailure {
            Timber.d(it)
        }
    }

    suspend fun install() = withContext(Dispatchers.IO) {
        val deleteZipFile = userPreferencesRepository
            .data.first().deleteZipFile

        val callback = object : IInstallCallback.Stub() {
            override fun console(msg: String) {
                console.add(msg)
            }
            override fun onSuccess(id: String) {
                event = Event.SUCCEEDED
                getLocal(id)

                if (deleteZipFile) {
                    deleteBySu()
                }
            }
            override fun onFailure() {
                event = Event.FAILED
            }
        }

        console.add("- Installing ${zipFile.name}")
        ProviderCompat.moduleManager.install(zipFile.path, callback)

        context.tmpDir.deleteRecursively()
    }

    private fun getLocal(id: String) {
        viewModelScope.launch {
            modulesRepository.getLocal(id)
        }
    }

    private fun deleteBySu() = runCatching {
        ProviderCompat.fileManager.deleteOnExit(zipFile.path)
    }.onFailure {
        Timber.e(it)
    }.onSuccess {
        Timber.d("deleteOnExit: $it")
    }

    companion object {
        fun putPath(path: File) =
            ModulesScreen.Install.route.replace(
                "{path}", Uri.encode(path.absolutePath)
            )

        fun getPath(savedStateHandle: SavedStateHandle) =
            Uri.decode(
                checkNotNull(savedStateHandle["path"])
            ).let(::File)
    }
}