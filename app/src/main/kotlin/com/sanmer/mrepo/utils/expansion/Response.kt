package com.sanmer.mrepo.utils.expansion

fun <T> runRequest(
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

fun <T> runRequest(
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