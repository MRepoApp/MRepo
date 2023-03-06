package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.provider.local.ModuleUtils
import com.sanmer.mrepo.utils.MediaStoreUtils.copyTo
import com.sanmer.mrepo.utils.MediaStoreUtils.displayName
import kotlinx.coroutines.launch
import timber.log.Timber

class InstallViewModel : ViewModel() {
    val console = mutableStateListOf<String>()

    val state = object : Status.State(initialState = Event.LOADING) {
        override fun setSucceeded(value: Any?) {
            super.setSucceeded(value)
            Status.Local.setSucceeded()
        }

        override fun setFailed(value: Any?) {
            super.setFailed(value)
            value?.let { send(it.toString())}
        }
    }

    init {
        Timber.d("InstallViewModel init")

        /** Since the data is held by the ViewModel,
         * it is necessary to use the Local fake update to
         * notify the ViewModel to update the data.
         * */
        Status.Local.setLoading()
    }

    fun send(message: String) = console.add("- $message")
    fun clear() = console.clear()

    private val onSucceeded: (LocalModule) -> Unit = {
        viewModelScope.launch {
            ModuleManager.insertLocal(it)
            state.setSucceeded()
        }
    }

    fun install(
        context: Context,
        path: Uri
    ) {
        val file = context.cacheDir.resolve("install.zip")
        path.copyTo(file)
        send("Copying zip to temp directory")
        send("Installing ${path.displayName}")

        ModuleUtils.install(
            context = context,
            zipFile = file,
            onConsole = { console.add(it) },
            onSucceeded = onSucceeded,
            onFailed = {
                state.setFailed()
            }
        )
    }
}