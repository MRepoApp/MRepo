package dev.sanmer.mrepo.impl

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        Log.e(TAG, "exec<$this>", e)
        Result.failure(e)
    }

    suspend fun String.submit() =
        withContext(Dispatchers.IO) { exec() }

    suspend fun String.submit(
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "submit: ${this@submit}")
            val process = ProcessBuilder("sh", "-c", this@submit).start()
            val output = process.inputStream.bufferedReader()
            val error = process.errorStream.bufferedReader()

            withContext(Dispatchers.IO) {
                output.forEachLine {
                    Log.d(TAG, "output: $it")
                    stdout(it)
                }
            }

            withContext(Dispatchers.IO) {
                error.forEachLine {
                    Log.d(TAG, "stderr: $it")
                    stderr(it)
                }
            }

            when {
                process.waitFor().ok() -> Result.success(true)
                else -> Result.failure(RuntimeException())
            }
        } catch (e: Throwable) {
            Log.e(TAG, "submit<${this@submit}>", e)
            Result.failure(e)
        }
    }

    private fun Int.ok() = this == 0
}