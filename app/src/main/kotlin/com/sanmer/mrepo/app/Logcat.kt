package com.sanmer.mrepo.app

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime

object Logcat {
    val logfile get() = "MRepo_${LocalDateTime.now()}.log"

    private suspend fun dump(context: Context) = withContext(Dispatchers.IO) {
        val uid = context.applicationInfo.uid
        val command = arrayOf(
            "logcat",
            "-d",
            "--uid=${uid}"
        )

        try {
            val process = Runtime.getRuntime().exec(command)

            val result = process.inputStream.use { stream ->
                stream.reader().readLines()
                    .filterNot { it.startsWith("------") }
                    .joinToString("\n")
            }

            process.waitFor()

            result.trim()
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun write(
        context: Context,
        writer: (ByteArray) -> Unit
    ) = withContext(Dispatchers.IO) {
        val logs = dump(context)
        writer(logs.toByteArray())
    }

    suspend fun writeTo(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            cr.openOutputStream(uri)?.use {
                write(context, it::write)
            }
        }.onFailure {
            Timber.d(it)
        }
    }
}