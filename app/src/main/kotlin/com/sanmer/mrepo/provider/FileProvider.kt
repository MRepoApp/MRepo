package com.sanmer.mrepo.provider

import android.content.Context
import com.sanmer.mrepo.app.Status
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber
import java.io.File

object FileProvider {
    private val fileSystemManager get() = try {
        FileSystemManager.getRemote(SuProvider.Root.fileSystemService)
    } catch (e: Exception) {
        FileSystemManager.getLocal()
    }

    private val fs get() = when {
        EnvProvider.isRoot -> fileSystemManager
        else -> FileSystemManager.getLocal()
    }

    fun init(context: Context) {
        Timber.d("FileProvider init")

        if (EnvProvider.isRoot) {
            SuProvider.init(context)
        } else {
            Status.Provider.setFailed()
        }
    }

    fun getFile(path: File) = run { fs.getFile(path.absolutePath) }
    fun getFile(path: String) = run { fs.getFile(path) }
    fun getFile(parent: String, child: String) = run { fs.getFile(parent, child) }
    fun getFile(parent: File, child: String) = run { fs.getFile(parent, child) }
}