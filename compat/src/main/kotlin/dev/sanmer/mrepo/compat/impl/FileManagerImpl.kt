package dev.sanmer.mrepo.compat.impl

import dev.sanmer.mrepo.compat.stub.IFileManager
import java.io.File

internal class FileManagerImpl : IFileManager.Stub() {
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