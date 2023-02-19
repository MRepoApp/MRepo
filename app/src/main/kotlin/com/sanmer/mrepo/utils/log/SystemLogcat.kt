package com.sanmer.mrepo.utils.log

import android.util.Log

class SystemLogcat(
    uid: Int
) {
    private val command = arrayOf(
        "logcat",
        "-d",
        "-v",
        "threadtime",
        "--uid",
        uid.toString()
    )

    fun dumpCrash(): List<String> {
        return try {
            val process = Runtime.getRuntime().exec(command)

            val result = process.inputStream.use { stream ->
                stream.reader().readLines()
                    .filterNot { it.startsWith("------") }
            }

            process.waitFor()
            result
        } catch (e: Exception) {
            listOf()
        }
    }

    companion object {
        fun String.toLogItem(): LogItem {
            return try {
                split(": ", limit = 2).let { list ->
                    val item = list.first()
                        .split(" ")
                        .filter { it != "" }

                    LogItem(
                        priority = when (item[4]) {
                            "V" -> Log.VERBOSE
                            "D" -> Log.DEBUG
                            "I" -> Log.INFO
                            "W" -> Log.WARN
                            "E" -> Log.ERROR
                            else -> 0
                        },
                        time = "${item[0]} ${item[1]}",
                        process = "${item[2]}-${item[3]}",
                        tag = item[5],
                        message = list.last()
                    )
                }
            } catch (e: Exception) {
                LogItem(
                    priority = 0,
                    time = "",
                    process = "",
                    tag = "",
                    message = ""
                )
            }
        }
    }
}