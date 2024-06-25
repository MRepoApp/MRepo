package dev.sanmer.mrepo.compat

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.aStore
import android.system.Os
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.IOException

object aStoreCompat {
    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.createaStoreUri(
        file: File,
        collection: Uri = aStore.Downloads.getContentUri(aStore.VOLUME_EXTERNAL),
        mimeType: String
    ): Uri {
        val entry = ContentValues().apply {
            put(aStore.aColumns.DISPLAY_NAME, file.name)
            put(aStore.aColumns.RELATIVE_PATH, file.parent)
            put(aStore.aColumns.MIME_TYPE, mimeType)
        }

        return contentResolver.insert(collection, entry) ?: throw IOException("Cannot insert $file")
    }

    private fun createDownloadUri(
        path: String
    ) = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    ).let {
        val file = File(it, path)
        file.parentFile?.apply { if (!exists()) mkdirs() }
        file.toUri()
    }

    fun Context.createDownloadUri(
        path: String,
        mimeType: String
    ) = when {
        BuildCompat.atLeastR -> runCatching {
            createaStoreUri(
                file = File(Environment.DIRECTORY_DOWNLOADS, path),
                mimeType = mimeType
            )
        }.getOrElse {
            createDownloadUri(path)
        }
        else -> createDownloadUri(path)
    }

    private fun ContentResolver.queryString(uri: Uri, columnName: String): String? {
        query(
            uri,
            arrayOf(columnName),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(
                    cursor.getColumnIndexOrThrow(columnName)
                )
            }
        }

        return null
    }

    fun Context.getOwnerPackageNameForUri(uri: Uri): String? {
        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        return when {
            uri.authority == aStore.AUTHORITY -> {
                contentResolver.queryString(
                    uri = uri,
                    columnName = aStore.aColumns.OWNER_PACKAGE_NAME
                )
            }
            else -> {
                uri.authority?.let {
                    packageManager.resolveContentProvider(
                        it, 0
                    )?.packageName
                }
            }
        }
    }

    fun Context.getDisplayNameForUri(uri: Uri): String {
        if (uri.scheme == "file") {
            return uri.toFile().name
        }

        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        return contentResolver.queryString(
            uri = uri,
            columnName = aStore.aColumns.DISPLAY_NAME
        ) ?: uri.toString()
    }

    fun Context.getPathForUri(uri: Uri): String {
        if (uri.scheme == "file") {
            return uri.toFile().path
        }

        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        contentResolver.openFileDescriptor(
            getDocumentUri(this, uri),
            "r"
        )?.use {
            return Os.readlink("/proc/self/fd/${it.fd}")
        }

        return uri.toString()
    }

    fun getDocumentUri(context: Context, uri: Uri): Uri {
        return when {
            DocumentsContract.isTreeUri(uri) -> {
                DocumentFile.fromTreeUri(context, uri)?.uri ?: uri
            }
            else -> {
                uri
            }
        }
    }

    fun Context.copyToDir(uri: Uri, dir: File): File {
        if (!dir.exists()) dir.mkdirs()
        val tmp = dir.resolve(getDisplayNameForUri(uri))

        contentResolver.openInputStream(uri)?.buffered()?.use { input ->
            tmp.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tmp
    }
}