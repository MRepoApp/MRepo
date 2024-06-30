package dev.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.compat.MediaStoreCompat.copyToDir
import dev.sanmer.mrepo.compat.MediaStoreCompat.getPathForUri
import dev.sanmer.mrepo.content.State
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.utils.extensions.now
import dev.sanmer.mrepo.utils.extensions.tmpDir
import dev.sanmer.su.wrap.ThrowableWrapper
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
    private val localRepository: LocalRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val mm get() = Compat.moduleManager

    private val logs = mutableListOf<String>()
    val console = mutableStateListOf<String>()
    var event by mutableStateOf(Event.Installing)
        private set

    val logfile by lazy {
        "Install_${LocalDateTime.now()}.log"
    }

    init {
        Timber.d("InstallViewModel init")
    }

    fun reboot() = mm.reboot()

    suspend fun writeLogsTo(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            cr.openOutputStream(uri)?.use {
                it.write(logs.joinToString(separator = "\n").toByteArray())
            }
        }.onFailure {
            Timber.d(it)
        }
    }

    suspend fun loadModule(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val userPreferences = userPreferencesRepository.data.first()

        if (!Compat.init(userPreferences.workingMode)) {
            event = Event.Failed
            console.add("- Service is not available")
            return@withContext
        }

        val path = context.getPathForUri(uri)
        Timber.d("path = $path")

        mm.getModuleInfo(path)?.let {
            Timber.d("module = $it")
            install(path)

            return@withContext
        }

        console.add("- Copying zip to temp directory")
        val tmpFile = context.copyToDir(uri, context.tmpDir)

        mm.getModuleInfo(tmpFile.path)?.let {
            Timber.d("module = $it")
            install(tmpFile.path)

            return@withContext
        }

        event = Event.Failed
        console.add("- Zip parsing failed")
    }

    private suspend fun install(zipPath: String) = withContext(Dispatchers.IO) {
        val userPreferences = userPreferencesRepository.data.first()
        val deleteZipFile = userPreferences.deleteZipFile
        val zipFile = File(zipPath)

        val callback = object : IInstallCallback.Stub() {
            override fun onStdout(msg: String) {
                console.add(msg)
                logs.add(msg)
            }

            override fun onStderr(msg: String) {
                logs.add(msg)
            }

            override fun onSuccess(module: LocalModule?) {
                event = Event.Succeeded
                module?.let(::insertLocal)

                if (deleteZipFile) {
                    deleteBySu(zipPath)
                }
            }

            override fun onFailure(error: ThrowableWrapper) {
                event = Event.Failed
                Timber.e(error.original)
            }
        }

        console.add("- Installing ${zipFile.name}")
        mm.install(zipPath, callback)
    }

    private fun insertLocal(module: LocalModule) {
        viewModelScope.launch {
            localRepository.insertLocal(
                module.copy(state = State.Update)
            )
        }
    }

    private fun deleteBySu(zipPath: String) {
        runCatching {
            mm.deleteOnExit(zipPath)
        }.onFailure {
            Timber.e(it)
        }
    }

    enum class Event {
        Installing,
        Succeeded,
        Failed;

        companion object {
            val Event.isInstalling get() = this == Installing
            val Event.isSucceeded get() = this == Succeeded
            val Event.isFailed get() = this == Failed
            val Event.isFinished get() = isSucceeded || isFailed
        }
    }
}