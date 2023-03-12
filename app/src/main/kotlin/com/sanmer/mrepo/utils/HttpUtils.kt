package com.sanmer.mrepo.utils

import com.sanmer.mrepo.utils.expansion.runRequest
import com.sanmer.mrepo.utils.MediaStoreUtils.newOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
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

        val get: (ResponseBody) -> File = {
            val buffer = ByteArray(2048)
            val input = it.byteStream()

            val output = out.newOutputStream()

            val all = it.contentLength()
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