package com.sanmer.mrepo.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.sanmer.mrepo.provider.FileServer
import java.io.File

object MediaStoreUtils {
    private lateinit var cr: ContentResolver
    private val fs = FileServer

    fun init(context: Context) {
        cr = context.contentResolver
    }

    val Uri.displayName: String get() {
        if (scheme == "file") {
            return toFile().name
        }
        require(scheme == "content") { "Uri lacks 'content' scheme: $this" }
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        cr.query(this, projection, null, null, null)?.use { cursor ->
            val displayNameColumn = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(displayNameColumn)
            }
        }
        return this.toString()
    }

    fun Uri.copy(new: File) {
        if (new.exists()) new.delete()
        cr.openInputStream(this)?.let { input ->
            cr.openOutputStream(new.toUri())?.let { output ->
                output.write(input.readBytes())
                output.flush()
                output.close()
            }
            input.close()
        }
    }

    fun File.copy(new: File) {
        if (new.exists()) new.delete()
        val input = fs.getFile(absolutePath).newInputStream()
        val output = fs.getFile(new.absolutePath).newOutputStream()
        output.write(input.readBytes())
        output.flush()
        output.close()
        input.close()
    }

    fun File.parent(): File {
        return File(parent!!)
    }

    fun String.toFile(): File? {
        return try {
            File(this)
        } catch (e: Exception) {
            null
        }
    }
}