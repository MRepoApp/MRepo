package com.sanmer.mrepo.utils.extensions

fun isHTML(text: String): Boolean {
    return "<html\\s*>|<head\\s*>|<body\\s*>|<!doctype\\s*html\\s*>"
        .toRegex()
        .containsMatchIn(text)
}

inline fun <reified T> runRequest(
    run: () -> retrofit2.Response<T>
): Result<T> = try {
    val response = run()
    if (response.isSuccessful) {
        val data = response.body()
        if (data != null) {
            Result.success(data)
        }else {
            Result.failure(NullPointerException())
        }
    } else {
        val errorBody = response.errorBody()
        val error = errorBody?.string() ?: "404 Not Found"

        if (isHTML(error)) {
            Result.failure(RuntimeException("404 Not Found"))
        } else {
            Result.failure(RuntimeException(error))
        }
    }
} catch (e: Exception) {
    Result.failure(e)
}

inline fun <T, reified R> runRequest(
    run: () -> retrofit2.Response<T>,
    convert: (T) -> R
): Result<R> = try {
    val response = run()
    if (response.isSuccessful) {
        val data = response.body()
        if (data != null) {
            Result.success(convert(data))
        }else {
            Result.failure(NullPointerException())
        }
    } else {
        val errorBody = response.errorBody()
        val error = errorBody?.string() ?: "404 Not Found"

        if (isHTML(error)) {
            Result.failure(RuntimeException("404 Not Found"))
        } else {
            Result.failure(RuntimeException(error))
        }
    }
} catch (e: Exception) {
    Result.failure(e)
}

inline fun <reified T> runRequest(
    get: (okhttp3.ResponseBody) -> T,
    run: () -> okhttp3.Response
): Result<T> = try {
    val response = run()
    if (response.isSuccessful) {
        val data = response.body()
        if (data != null) {
            Result.success(get(data))
        } else {
            Result.failure(NullPointerException())
        }
    } else {
        val errorBody = response.body()
        val error = errorBody?.string() ?: "404 Not Found"

        if (isHTML(error)) {
            Result.failure(RuntimeException("404 Not Found"))
        } else {
            Result.failure(RuntimeException(error))
        }
    }
} catch (e: Exception) {
    Result.failure(e)
}