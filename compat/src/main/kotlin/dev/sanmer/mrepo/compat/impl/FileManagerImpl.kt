package dev.sanmer.mrepo.compat.impl

import dev.sanmer.mrepo.compat.stub.IFileManager
import java.io.File

internal class FileManagerImpl : IFileManager.Stub() {
    override fun deleteOnExit(path: String) = with(File(path)) {
        when {
            !exists() -> false
            isFile -> delete()
            isDirectory -> deleteRecursively()
            else -> false
        }
    }
}