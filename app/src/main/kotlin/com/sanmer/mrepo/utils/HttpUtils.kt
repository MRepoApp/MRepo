package com.sanmer.mrepo.utils

import com.sanmer.mrepo.utils.MediaStoreUtils.newOutputStream
import com.sanmer.mrepo.utils.expansion.runRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File

object HttpUtils {
    suspend fun <T>request(
        url: String,
        get: (ResponseBody) -> T
    ) =  withContext(Dispatchers.IO) {
        runRequest(get) {
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

    suspend fun downloader(
        url: String,
        out: File,
        onProgress: (Float) -> Unit
    ): Result<File> {
        out.parentFile!!.let {
            if (!it.exists())
                it.mkdirs()
        }

        val get: (ResponseBody) -> File = { body ->
            val buffer = ByteArray(2048)
            val input = body.byteStream()

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

            out
        }

        return request(
            url = url,
            get = get
        )
    }
}