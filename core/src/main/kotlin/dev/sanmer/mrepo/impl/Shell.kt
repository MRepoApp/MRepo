package dev.sanmer.mrepo.impl

import android.util.Log

internal object Shell {
    private const val TAG = "Shell"

    fun String.exec(): Result<String> =
        runCatching {
            Log.d(TAG, "exec: $this")
            val process = ProcessBuilder("sh", "-c", this).start()
            val output = process.inputStream.bufferedReader().readText()
                .removeSurrounding("", "\n")

            val error = process.errorStream.bufferedReader().readText()
                .removeSurrounding("", "\n")

            require(process.waitFor().ok()) { error }
            Log.d(TAG, "output: $output")

            output
        }.onFailure {
            Log.e(TAG, Log.getStackTraceString(it))
        }

    fun String.exec(
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ) = runCatching {
        Log.d(TAG, "exec: $this")
        val process = ProcessBuilder("sh", "-c", this).start()
        val output = process.inputStream.bufferedReader()
        val error = process.errorStream.bufferedReader()

        output.forEachLine {
            Log.d(TAG, "output: $it")
            stdout(it)
        }

        error.forEachLine {
            Log.d(TAG, "error: $it")
            stderr(it)
        }

        require(process.waitFor().ok())
    }.onFailure {
        Log.e(TAG, Log.getStackTraceString(it))
    }

    private fun Int.ok() = this == 0
}