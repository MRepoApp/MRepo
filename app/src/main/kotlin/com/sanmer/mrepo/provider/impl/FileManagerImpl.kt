package com.sanmer.mrepo.provider.impl

import com.sanmer.mrepo.provider.stub.IFileManager
import java.io.File

class FileManagerImpl : IFileManager.Stub() {
    override fun deleteOnExit(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) return false

        if (file.isFile) {
            return file.delete()
        }

        if (file.isDirectory) {
            return file.deleteRecursively()
        }

        return false
    }
}