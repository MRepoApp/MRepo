package com.sanmer.mrepo.utils.log

data class LogItem(
    val priority: Int,
    val time: String,
    val process: String,
    val tag: String,
    val message: String
)
