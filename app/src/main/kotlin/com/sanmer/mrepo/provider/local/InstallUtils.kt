package com.sanmer.mrepo.provider.local

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.utils.MediaStoreUtils.copyTo
import com.sanmer.mrepo.utils.MediaStoreUtils.displayName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object InstallUtils : Status.State() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val console = mutableStateListOf<String>()

    /** Since the data is held by the ViewModel,
     * it is necessary to use the Local fake update to
     * notify the ViewModel to update the data */
    override fun setLoading(value: Any?) {
        super.setLoading(value)
        Status.Local.setLoading()
    }
    override fun setSucceeded(value: Any?) {
        super.setSucceeded(value)
        Status.Local.setSucceeded()
    }

    fun clear() {
        console.clear()
        setNon()
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
        setLoading()

        val file = context.cacheDir.resolve("install.zip")
        path.copyTo(file)
        console.add("- Copying zip to temp directory")
        console.add("- Installing ${path.displayName}")

        ModuleUtils.install(
            context = context,
            zipFile = file,
            onConsole = {
                console.add(it)
            },
            onSucceeded = { module ->
                coroutineScope.launch {
                    ModuleManager.insertLocal(module)
                    setSucceeded()
                }
            },
            onFailed = {
                setFailed()
            }
        )
    }
}