package dev.sanmer.mrepo.impl

import android.util.Log

internal object Shell {
    private const val TAG = "Shell"

    fun String.exec(): Result<String> = try {
        Log.d(TAG, "exec: $this")
        val process = ProcessBuilder("sh", "-c", this).start()
        val output = process.inputStream.bufferedReader().readText()
            .removeSurrounding("", "\n")

        val error = process.errorStream.bufferedReader().readText()
            .removeSurrounding("", "\n")

        when {
            process.waitFor().ok() -> {
                Log.d(TAG, "output: $output")
                Result.success(output)
            }
            else -> {
                Log.d(TAG, "error: $error")
                Result.failure(RuntimeException(error))
            }
        }
    } catch (e: Throwable) {
        Log.e(TAG, Log.getStackTraceString(e))
        Result.failure(e)
    }

    fun String.exec(
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ) = try {
        Log.d(TAG, "submit: ${this@exec}")
        val process = ProcessBuilder("sh", "-c", this@exec).start()
        val output = process.inputStream.bufferedReader()
        val error = process.errorStream.bufferedReader()

        output.forEachLine {
            Log.d(TAG, "output: $it")
            stdout(it)
        }

        error.forEachLine {
            Log.d(TAG, "stderr: $it")
            stderr(it)
        }

        when {
            process.waitFor().ok() -> Result.success(true)
            else -> Result.failure(RuntimeException())
        }
    } catch (e: Throwable) {
        Log.e(TAG, Log.getStackTraceString(e))
        Result.failure(e)
    }

    private fun Int.ok() = this == 0
}