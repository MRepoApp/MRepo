package com.sanmer.mrepo.utils.module

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.app.status.Event
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.utils.MediaStoreUtils.copy
import com.sanmer.mrepo.utils.MediaStoreUtils.displayName
import java.io.File

object InstallUtils {
    val console = mutableStateListOf<String>()
    var event by mutableStateOf(Event.NON)
    val isFinish get() = event != Event.LOADING
    val isSuccess get() = event == Event.SUCCEEDED

    fun clear() {
        console.clear()
        event = Event.NON
    }

    private fun install(
        context: Context,
        path: File,
        name: String
    ) {
        event = Event.LOADING

        console.add("- Installing $name")
        ModuleUtils.install(
            context = context,
            zipFile = path,
            onConsole = {
                console.add(it)
            },
            onSucceeded = {
                Constant.insertLocal(it)
                event = Event.SUCCEEDED
            },
            onFailed = {
                event = Event.FAILED
            }
        )
    }

    fun install(
        context: Context,
        path: File
    ) {
        val file = context.cacheDir.resolve("install.zip")

        console.add("- Copying zip to temp directory")
        path.copy(file)
        install(
            context = context,
            path = file,
            name = path.name
        )
    }

    fun install(
        context: Context,
        path: Uri
    ) {
        val file = context.cacheDir.resolve("install.zip")

        console.add("- Copying zip to temp directory")
        path.copy(file)
        install(
            context = context,
            path = file,
            name = path.displayName
        )
    }
}