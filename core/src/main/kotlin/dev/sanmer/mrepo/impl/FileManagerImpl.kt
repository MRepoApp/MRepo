package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.stub.IFileManager
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