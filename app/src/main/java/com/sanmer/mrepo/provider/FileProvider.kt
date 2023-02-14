package com.sanmer.mrepo.provider

import android.content.Context
import com.sanmer.mrepo.provider.fs.LibSuService
import com.topjohnwu.superuser.nio.FileSystemManager
import java.io.File

object FileProvider {
    private val fs get() = when {
        EnvProvider.isRoot -> LibSuService.fileSystemManager
        else -> FileSystemManager.getLocal()
    }

    fun init(context: Context) {
        EnvProvider.onRoot { LibSuService.start(context) }
    }

    fun getFile(path: File) = run { fs.getFile(path.absolutePath) }
    fun getFile(path: String) = run { fs.getFile(path) }
    fun getFile(parent: String, child: String) = run { fs.getFile(parent, child) }
    fun getFile(parent: File, child: String) = run { fs.getFile(parent, child) }
}