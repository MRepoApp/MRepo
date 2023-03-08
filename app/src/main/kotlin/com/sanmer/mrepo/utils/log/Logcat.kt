package com.sanmer.mrepo.utils.log

import android.content.Context
import android.util.Log
import com.sanmer.mrepo.App
import com.sanmer.mrepo.utils.expansion.now
import com.sanmer.mrepo.utils.expansion.shareFile
import kotlinx.datetime.LocalDateTime

object Logcat {
    private val context by lazy { App.context }
    private val uid by lazy { context.applicationInfo.uid }
    private val date by lazy { LocalDateTime.now() }

    private var fileName = "app_${date}.log"
    private val logFile by lazy { context.cacheDir.resolve("log/${fileName}") }

    init {
        val logDir = context.cacheDir.resolve("log")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        initLogFile()
    }

    private fun initLogFile() {
        val appLogs = context.cacheDir.resolve("log").walkBottomUp().filter {
            it.name.startsWith("app")
        }.sortedBy {
            it.name.toDateTime()
        }

        if (appLogs.count() == 0) return
        if (appLogs.count() > 3) appLogs.first().delete()

        val last = appLogs.last()
        val date = last.name.toDateTime()
        val isToday = date.date == LocalDateTime.now().date
        if (isToday) {
            fileName = last.name
        }
    }

    fun getCurrent(): List<String> = try {
        val command = arrayOf(
            "logcat",
            "-d",
            "-v",
            "threadtime",
            "--uid",
            uid.toString()
        )

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

    fun readLogs(): List<LogText> {
        return if (logFile.exists()) {
            val logs = mutableListOf<LogText>()
            logFile.readLines().forEach { text ->
                runCatching {
                    LogText.parse(text)
                }.onSuccess {
                    logs.add(it)
                }.onFailure {
                    val last = logs.last()
                    val new = last.copy(message = "${last.message}\n${text}")
                    logs[logs.size - 1] = new
                }
            }

            logs.toList()
        } else {
            emptyList()
        }
    }

    fun writeLog(log: LogText) {
        logFile.appendText("$log\n")
    }

    fun shareLogs(context: Context) {
        context.shareFile(logFile, "text/plain")
    }

    fun Collection<String>.toLogTextList(): List<LogText> {
        val tmp = map { it.split(": ", limit = 2) }
        val tags = tmp.map { it.first() }.distinct()
        val logs = tags.map { tag ->
            val message = tmp.filter {
                it.first() == tag
            }.map { it.last() }.reduceOrNull { b, e ->
                "$b\n$e"
            }

            return@map tag.toLogText().copy(message = message ?: "")
        }

        return logs
    }

    private fun String.toLogText(): LogText = try {
        split(": ", limit = 2).let { list ->
            val item = list.first()
                .split(" ")
                .filter { it != "" }

            LogText(
                priority = item[4].toLogPriority(),
                time = "${item[0]} ${item[1]}",
                process = "${item[2]}-${item[3]}",
                tag = item[5],
                message = list.last()
            )
        }
    } catch (e: Exception) {
        LogText(0, "", "", "","")
    }

    fun String.toLogPriority() = when (this) {
        "V" -> Log.VERBOSE
        "D" -> Log.DEBUG
        "I" -> Log.INFO
        "W" -> Log.WARN
        "E" -> Log.ERROR
        else -> 0
    }

    fun Int.toTextPriority() = when (this) {
        Log.VERBOSE -> "V"
        Log.DEBUG -> "D"
        Log.INFO -> "I"
        Log.WARN -> "W"
        Log.ERROR -> "E"
        else -> "N"
    }

    private fun String.toDateTime() = LocalDateTime.parse(
        replace("app_", "")
            .replace(".log", "")
    )
}