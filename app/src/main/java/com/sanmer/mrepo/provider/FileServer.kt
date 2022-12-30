package com.sanmer.mrepo.provider

import android.content.Context
import com.sanmer.mrepo.provider.libsu.SuFileService
import java.io.File

object FileServer {
    private val fs get() = SuFileService.getFileSystemManager()

    fun init(context: Context) {
        SuFileService.init(context)
    }

    fun stop(context: Context) {
        SuFileService.stop(context)
    }

    fun getFile(path: File) = run { fs.getFile(path.absolutePath) }
    fun getFile(path: String) = run { fs.getFile(path) }
    fun getFile(parent: String, child: String) = run { fs.getFile(parent, child) }
    fun getFile(parent: File, child: String) = run { fs.getFile(parent, child) }
}