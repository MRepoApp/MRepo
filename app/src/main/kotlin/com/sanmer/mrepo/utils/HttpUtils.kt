package com.sanmer.mrepo.utils

import com.sanmer.mrepo.utils.MediaStoreUtils.newOutputStream
import com.sanmer.mrepo.utils.expansion.runRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File

object HttpUtils {
    suspend inline fun <reified T> request(
        url: String,
        crossinline get: (ResponseBody) -> T
    ) =  withContext(Dispatchers.IO) {
        runRequest(get = get) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).execute()
        }
    }

    suspend fun requestString(
        url: String
    ): Result<String> = request(
        url = url,
        get = { it.string() }
    )

    suspend inline fun <reified T> requestJson(
        url: String
    ): Result<T> = request(url) {
        val adapter = Moshi.Builder()
            .build()
            .adapter<T>()

        return@request adapter.fromJson(it.string())!!
    }

    suspend fun downloader(
        url: String,
        out: File,
        onProgress: (Float) -> Unit
    ): Result<File> = request(url) { body ->
        val buffer = ByteArray(2048)
        val input = body.byteStream()

        out.parentFile!!.let {
            if (!it.exists()) it.mkdirs()
        }
        val output = out.newOutputStream()

        val all = body.contentLength()
        var finished: Long = 0
        var readying: Int

        while (input.read(buffer).also { readying = it } != -1) {
            output?.write(buffer, 0, readying)
            finished += readying.toLong()

            val progress = (finished * 1.0 / all).toFloat()
            onProgress(progress)
        }

        output?.flush()
        output?.close()
        input.close()

        return@request out
    }
}