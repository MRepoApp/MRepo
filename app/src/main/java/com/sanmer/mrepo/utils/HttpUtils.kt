package com.sanmer.mrepo.utils

import com.sanmer.mrepo.provider.FileServer
import com.sanmer.mrepo.utils.MediaStoreUtils.parent
import okhttp3.*
import java.io.File
import java.io.IOException

object HttpUtils {
    private val fs = FileServer
    private fun request(
        url: String, callback: Callback
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }

    private fun request(
        url: String,
        onSucceeded: (ResponseBody) -> Unit = {},
        onFailed: (String?) -> Unit = {},
        onFinished: () -> Unit = {},
    ) {
        request(url = url, callback = object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.let(onSucceeded)
                } else {
                    onFailed("${response.body()?.string()}")
                }
                onFinished()
            }
            override fun onFailure(call: Call, e: IOException) {
                onFailed(e.message)
                onFinished()
            }
        })
    }

    fun downloader(
        url: String,
        onProgress: (Float) -> Unit,
        onSuccess: () -> Unit = {},
        onFail: (String?) -> Unit = {},
        onFinish: () -> Unit = {},
        path: File
    ) {
        val parent = path.parent()
        if (!parent.exists()) parent.mkdirs()
        request(
            url = url,
            onSucceeded = { body ->
                val buffer = ByteArray(2048)
                val input = body.byteStream()
                val output = fs.getFile(path).newOutputStream()
                val all = body.contentLength()
                var finished: Long = 0
                var readying: Int

                while (input.read(buffer).also { readying = it } != -1) {
                    output.write(buffer, 0, readying)
                    finished += readying.toLong()

                    val progress = (finished * 1.0 / all).toFloat()
                    onProgress(progress)
                }

                output.flush()
                output.close()
                input.close()
                onSuccess()
            },
            onFailed = {
                onFail(it)
            },
            onFinished = onFinish
        )
    }
}