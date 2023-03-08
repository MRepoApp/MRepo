package com.sanmer.mrepo.utils.log

import com.sanmer.mrepo.utils.log.Logcat.toLogPriority
import com.sanmer.mrepo.utils.log.Logcat.toTextPriority

data class LogText(
    val priority: Int,
    val time: String,
    val process: String,
    val tag: String,
    val message: String
) {
    override fun toString(): String {
        return "[ $time $process ${priority.toTextPriority()}/$tag ] $message"
    }

    companion object {
        fun parse(text: String): LogText {
            val texts = text.split("/", limit = 2)
            val tmp1 = texts.first()
                .replace("[ ", "")
                .split(" ", limit = 4)
            val tmp2 = texts.last().split(" ] ", limit = 2)

            return LogText(
                priority = tmp1[3].toLogPriority(),
                time = "${tmp1[0]} ${tmp1[1]}",
                process = tmp1[2],
                tag = tmp2[0],
                message = tmp2[1]
            )
        }
    }
}
