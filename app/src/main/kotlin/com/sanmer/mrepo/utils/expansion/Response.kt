package com.sanmer.mrepo.utils.expansion

inline fun <reified T> runRequest(
    run: () -> retrofit2.Response<T>
): Result<T> = try {
    val response = run()
    if (response.isSuccessful) {
        val data = response.body()
        if (data != null) {
            Result.success(data)
        }else {
            Result.failure(NullPointerException("The data is null!"))
        }
    } else {
        val errorBody = response.errorBody()
        val error = errorBody?.string() ?: "404 Not Found"

        if ("</html>" in error) {
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
            Result.failure(NullPointerException("The data is null!"))
        }
    } else {
        val errorBody = response.body()
        val error = errorBody?.string() ?: "404 Not Found"

        if ("</html>" in error) {
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
            Result.failure(NullPointerException("The data is null!"))
        }
    } else {
        val errorBody = response.errorBody()
        val error = errorBody?.string() ?: "404 Not Found"

        if ("</html>" in error) {
            Result.failure(RuntimeException("404 Not Found"))
        } else {
            Result.failure(RuntimeException(error))
        }
    }
} catch (e: Exception) {
    Result.failure(e)
}