package com.sanmer.mrepo.utils.extensions

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.math.log10
import kotlin.math.pow

fun String.toFile() = File(this)

val File.isSymbolicLink get() = exists() && absolutePath != canonicalPath

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

fun Long.formatSize() = if (this < 0){
    "0 B"
} else {
    val units = listOf("B", "KB", "MB")
    val group = (log10(toDouble()) / log10(1024.0)).toInt()
    String.format("%.2f %s", this / 1024.0.pow(group.toDouble()), units[group])
}


@Throws(IOException::class)
fun File.unzip(folder: File, path: String = "", junkPath: Boolean = false) {
    inputStream().buffered().use {
        it.unzip(folder, path, junkPath)
    }
}

@Throws(IOException::class)
fun InputStream.unzip(folder: File, path: String, junkPath: Boolean) {
    try {
        val zin = ZipInputStream(this)
        var entry: ZipEntry
        while (true) {
            entry = zin.nextEntry ?: break
            if (!entry.name.startsWith(path) || entry.isDirectory) {
                // Ignore directories, only create files
                continue
            }
            val name = if (junkPath)
                entry.name.substring(entry.name.lastIndexOf('/') + 1)
            else
                entry.name

            val dest = File(folder, name)
            dest.parentFile!!.let {
                if (!it.exists())
                    it.mkdirs()
            }
            dest.outputStream().use { out -> zin.copyTo(out) }
        }
    } catch (e: IllegalArgumentException) {
        throw IOException(e)
    }
}