package com.sanmer.mrepo.utils.expansion

import java.io.File

fun String.toFile() = File(this)

val File.totalSize: Long get() {
    var size: Long = 0
    listFiles()?.forEach {
        if (!it.isDirectory && !it.isSymbolicLink) {
            size += it.length()
        } else if (it.isDirectory) {
            size += it.totalSize
        }
    }

    return size
}

val File.isSymbolicLink get() = exists() && absolutePath != canonicalPath