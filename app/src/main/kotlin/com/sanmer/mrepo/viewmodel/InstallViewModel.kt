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
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.navigation.graphs.ModulesScreen
import com.sanmer.mrepo.utils.extensions.now
import com.sanmer.mrepo.utils.extensions.tmpDir
import com.topjohnwu.superuser.CallbackList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor(
    private val localRepository: LocalRepository,
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

    suspend fun install() = withContext(Dispatchers.IO) {
        val deleteZipFile = userPreferencesRepository
            .data.first().deleteZipFile

        val msg = object : CallbackList<String?>() {
            override fun onAddElement(str: String?) {
                str?.let(console::add)
            }
        }

        console.add("- Installing ${zipFile.name}")

        val module = SuProvider.moduleManager
            .install(zipFile.path, msg)

        if (module != null) {
            event = Event.SUCCEEDED
            if (deleteZipFile || zipFile.startsWith(context.tmpDir)) {
                deleteBySu()
            }

            context.tmpDir.apply {
                if (exists()) deleteRecursively()
            }

            localRepository.insertLocal(module)
        } else {
            event = Event.FAILED
        }
    }

    private fun deleteBySu() = runCatching {
        SuProvider.fileSystemManager
            .getFile(zipFile.absolutePath).apply {
                if (exists()) delete()
            }
    }.onFailure {
        Timber.e(it)
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