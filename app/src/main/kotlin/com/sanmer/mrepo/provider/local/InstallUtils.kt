package com.sanmer.mrepo.provider.local

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.utils.MediaStoreUtils.copyTo
import com.sanmer.mrepo.utils.MediaStoreUtils.displayName
import java.io.File

object InstallUtils : Status.State() {
    val console = mutableStateListOf<String>()

    fun clear() {
        console.clear()
        setNon()
    }

    private fun install(
        context: Context,
        path: File,
        name: String
    ) {
        setLoading()

        console.add("- Installing $name")
        ModuleUtils.install(
            context = context,
            zipFile = path,
            onConsole = {
                console.add(it)
            },
            onSucceeded = { module ->
                if (module in Constant.local) {
                    Constant.updateLocal(module)
                } else {
                    Constant.insertLocal(module)
                }
                setSucceeded()
            },
            onFailed = {
                setFailed()
            }
        )
    }

    fun install(
        context: Context,
        path: File
    ) = install(
        context = context,
        path = path.toUri()
    )

    fun install(
        context: Context,
        path: Uri
    ) {
        val file = context.cacheDir.resolve("install.zip")

        console.add("- Copying zip to temp directory")
        path.copyTo(file)

        install(
            context = context,
            path = file,
            name = path.displayName
        )
    }
}