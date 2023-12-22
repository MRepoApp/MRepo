package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.provider.ProviderCompat
import com.sanmer.mrepo.provider.stub.IInstallCallback
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.utils.extensions.now
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
) : ViewModel() {
    val logs = mutableListOf<String>()
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
                it.write(logs.joinToString(separator = "\n").toByteArray())
            }
        }.onFailure {
            Timber.d(it)
        }
    }

    suspend fun install(zipPath: String, isReal: Boolean) = withContext(Dispatchers.IO) {
        val zipFile = File(zipPath)
        val deleteZipFile = userPreferencesRepository
            .data.first().deleteZipFile

        val callback = object : IInstallCallback.Stub() {
            override fun onStdout(msg: String) {
                console.add(msg)
                logs.add(msg)
            }

            override fun onStderr(msg: String) {
                logs.add(msg)
            }

            override fun onSuccess(id: String) {
                event = Event.SUCCEEDED
                getLocal(id)

                if (deleteZipFile && isReal) {
                    deleteBySu(zipPath)
                }
            }
            override fun onFailure() {
                event = Event.FAILED
            }
        }

        console.add("- Installing ${zipFile.name}")
        ProviderCompat.moduleManager.install(zipPath, callback)
    }

    private fun getLocal(id: String) {
        viewModelScope.launch {
            modulesRepository.getLocal(id)
        }
    }

    private fun deleteBySu(zipPath: String) {
        runCatching {
            ProviderCompat.fileManager.deleteOnExit(zipPath)
        }.onFailure {
            Timber.e(it)
        }.onSuccess {
            Timber.d("deleteOnExit: $it")
        }
    }
}