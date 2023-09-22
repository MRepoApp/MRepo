package com.sanmer.mrepo.app.utils

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.system.Os
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sanmer.mrepo.utils.extensions.tmpDir
import com.sanmer.mrepo.utils.extensions.toFile
import java.io.File

object MediaStoreUtils {
    @Composable
    fun PermissionState() {
        val permissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        SideEffect {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }

    fun getDisplayNameForUri(context: Context, uri: Uri): String {
        if (uri.scheme == "file") {
            return uri.toFile().name
        }

        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        val cr = context.contentResolver
        cr.query(uri, projection, null, null, null)?.use { cursor ->
            val displayNameColumn = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(displayNameColumn)
            }
        }

        return uri.toString()
    }

    fun getAbsolutePathForUri(context: Context, uri: Uri): String {
        if (uri.scheme == "file") {
            return uri.toFile().absolutePath
        }

        require(uri.scheme == "content") { "Uri lacks 'content' scheme: $uri" }

        val newUri = try {
            checkNotNull(DocumentFile.fromTreeUri(context, uri)?.uri)
        } catch (e: Exception) {
            uri
        }

        val cr =  context.contentResolver
        return cr.openFileDescriptor(newUri, "r")?.use {
            Os.readlink("/proc/self/fd/${it.fd}")
        } ?: uri.toString()
    }

    fun getAbsoluteFileForUri(context: Context, uri: Uri) =
        getAbsolutePathForUri(context, uri).toFile()

    fun copyToTmp(context: Context, uri: Uri): File {
        val filename = getDisplayNameForUri(context, uri)
        val file = context.tmpDir
            .apply { if (!exists()) mkdirs() }
            .resolve(filename)

        val cr =  context.contentResolver
        cr.openInputStream(uri)?.use { input ->
            cr.openOutputStream(file.toUri())?.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}