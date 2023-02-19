package com.sanmer.mrepo.utils

import com.sanmer.mrepo.utils.MediaStoreUtils.newOutputStream
import okhttp3.*
import java.io.File
import java.io.IOException

object HttpUtils {
    private fun request(
        url: String, callback: Callback
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun request(
        url: String,
        onSucceeded: (ResponseBody) -> Unit = {},
        onFailed: (String?) -> Unit = {},
        onFinished: () -> Unit = {},
    ) = request(url = url, callback = object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                response.body()?.let(onSucceeded)
            } else {
                onFailed(response.body()?.string())
            }
            onFinished()
        }
        override fun onFailure(call: Call, e: IOException) {
            onFailed(e.message)
            onFinished()
        }
    })

    fun downloader(
        url: String,
        onProgress: (Float) -> Unit,
        onSucceeded: () -> Unit = {},
        onFailed: (String?) -> Unit = {},
        onFinished: () -> Unit = {},
        path: File
    ) {
        path.parentFile!!.let {
            if (!it.exists())
                it.mkdirs()
        }

        val succeeded: (ResponseBody) -> Unit = { body ->
            runCatching {
                val buffer = ByteArray(2048)
                val input = body.byteStream()

                val output = path.newOutputStream()

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
                onSucceeded()
            }.onFailure {
                onFailed(it.message)
            }
        }

        request(
            url = url,
            onSucceeded = succeeded,
            onFailed = onFailed,
            onFinished = onFinished
        )
    }
}