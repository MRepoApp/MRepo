package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fox2code.androidansi.AnsiContext
import com.fox2code.androidansi.ktx.parseAsAnsiAnnotatedString
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.State
import com.sanmer.mrepo.app.utils.MediaStoreUtils.copyTo
import com.sanmer.mrepo.app.utils.MediaStoreUtils.displayName
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserDataRepository
import com.sanmer.mrepo.utils.expansion.now
import com.sanmer.mrepo.utils.expansion.shareFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val userDataRepository: UserDataRepository,
    private val suRepository: SuRepository
) : ViewModel() {

    val console = mutableStateListOf<ProcessedLine>()

    val state = object : State(initial = Event.LOADING) {
        override fun setFailed(value: Any?) {
            value?.let { send(it.toString())}
            super.setFailed(value)
        }
    }

    val suState get() = suRepository.state

    init {
        Timber.d("InstallViewModel init")
    }

    fun send(message: String) = console.add(ProcessedLine("- $message"))

    private val onSucceeded: (LocalModule) -> Unit = {
        viewModelScope.launch {
            localRepository.insertLocal(it)
            state.setSucceeded()
        }
    }

    fun install(
        context: Context,
        path: Uri
    ) {
        val file = context.cacheDir.resolve("install.zip")
        val ansiContext: AnsiContext = AnsiContext.DARK.asMutable()
        path.copyTo(file)
        send("Copying zip to temp directory")
        send("Installing ${path.displayName}")

        suRepository.install(
            zipFile = file,
            console = { console.add(ProcessedLine(it, ansiContext)) },
            onSuccess = {
                onSucceeded(it)
                context.cacheDir.resolve("install.zip").delete()
                if (userDataRepository.deleteZipFile) path.safeDelete(context)
            },
            onFailure = {
                state.setFailed()
            }
        )
    }

    private fun Uri.safeDelete(context: Context) = try {
        context.contentResolver.delete(this, null, null)
    } catch (e: Exception) {
        Timber.e(e)
    }

    fun sendLogFile(context: Context) {
        val text = console.joinToString(separator = "\n")
        val date = LocalDateTime.now()
        val logFile = context.cacheDir
            .resolve("log/module_${date}.log")
            .apply {
                writeText(text)
            }
        context.shareFile(logFile, "text/plain")
    }

    class ProcessedLine {
        val text: String
        val styledText: AnnotatedString?

        constructor(data: String, ansiContext: AnsiContext? = null) {
            if (ansiContext == null) {
                this.text = data
                this.styledText = null
                return
            }
            this.styledText = data.parseAsAnsiAnnotatedString(ansiContext)
            this.text = this.styledText.toString()
        }
    }
}