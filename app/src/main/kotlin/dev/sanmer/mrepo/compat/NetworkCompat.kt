package dev.sanmer.mrepo.compat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.sanmer.mrepo.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Cache
import okhttp3.ConnectionSpec
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber
import java.io.File
import java.io.OutputStream
import java.util.Locale

object NetworkCompat {
    private var cacheDirOrNull: File? = null
    private val cacheOrNull: Cache? get() = cacheDirOrNull?.let {
        Cache(File(it, "okhttp"), 10 * 1024 * 1024)
    }

    fun setCacheDir(dir: File) {
        cacheDirOrNull = dir
    }

    fun isHTML(text: String) =
        "<html\\s*>|<head\\s*>|<body\\s*>|<!doctype\\s*html\\s*>"
            .toRegex()
            .containsMatchIn(text)

    fun isUrl(url: String) = url.toHttpUrlOrNull() != null

    fun isBlobUrl(url: String) =
        "https://github.com/[^/]+/[^/]+/blob/.+"
            .toRegex()
            .matches(url)

    fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder().cache(cacheOrNull)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { Timber.i(it) }
                    .apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
            )
        } else {
            builder.connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
        }

        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
            request.header("User-Agent", "MRepo/${BuildConfig.VERSION_CODE}")
            request.header("Accept-Language", Locale.getDefault().toLanguageTag())
            chain.proceed(request.build())
        }

        return builder.build()
    }

    fun createRetrofit(): Retrofit.Builder {
        val client = createOkHttpClient()

        return Retrofit.Builder()
            .addConverterFactory(
                Json.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .client(client)
    }

    suspend fun <T> request(
        url: String,
        get: (ResponseBody, Headers) -> T
    ) = runRequest(get = get) {
        val client = createOkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .execute()
    }

    suspend fun requestString(url: String) =
        request(
            url = url,
            get = { body, _ ->
                body.string()
            }
        )

    suspend inline fun <reified T> requestJson(
        url: String
    ): Result<T> {
        val result = request(url) { body, _ ->
            Json.decodeFromStream<T>(body.byteStream())
        }

        if (result.isSuccess) {
            val json = result.getOrThrow()
            return Result.success(json)
        }

        return Result.failure(IllegalArgumentException())
    }

    suspend fun download(
        url: String,
        output: OutputStream,
        onProgress: (Float) -> Unit
    ) = request(url) { body, headers ->
        val buffer = ByteArray(2048)
        val input = body.byteStream()

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

        headers
    }

    suspend fun <T> runRequest(
        run: () -> retrofit2.Response<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = run()
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                }else {
                    Result.failure(NullPointerException())
                }

            } else {
                val error = response.errorBody()?.string() ?: "404 Not Found"
                if (isHTML(error)) {
                    Result.failure(RuntimeException("404 Not Found"))
                } else {
                    Result.failure(RuntimeException(error))
                }

            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun <T> runRequest(
        get: (ResponseBody, Headers) -> T,
        run: () -> okhttp3.Response
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = run()
            val body = response.body
            val headers = response.headers
            if (response.isSuccessful) {
                if (body != null) {
                    Result.success(get(body, headers))
                } else {
                    Result.failure(NullPointerException())
                }

            } else {
                val error = body?.string() ?: "404 Not Found"
                if (isHTML(error)) {
                    Result.failure(RuntimeException("404 Not Found"))
                } else {
                    Result.failure(RuntimeException(error))
                }

            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    object Compose {
        @Composable
        fun <T> runRequest(
            get: suspend () -> Result<T>
        ): Result<T>? {
            var result: Result<T>? by remember { mutableStateOf(null) }
            LaunchedEffect(true) { result = get() }
            return result
        }

        @Composable
        fun requestString(url: String) = runRequest(
            get = { NetworkCompat.requestString(url) }
        )

        @Composable
        inline fun <reified T> requestJson(url: String) = runRequest(
            get = { NetworkCompat.requestJson<T>(url) }
        )
    }
}