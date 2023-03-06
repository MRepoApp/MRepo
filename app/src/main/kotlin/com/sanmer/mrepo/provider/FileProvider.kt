package com.sanmer.mrepo.provider

import com.topjohnwu.superuser.nio.FileSystemManager
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

    fun getFile(path: File) = run { fs.getFile(path.absolutePath) }
    fun getFile(path: String) = run { fs.getFile(path) }
    fun getFile(parent: String, child: String) = run { fs.getFile(parent, child) }
    fun getFile(parent: File, child: String) = run { fs.getFile(parent, child) }
}