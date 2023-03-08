package com.sanmer.mrepo.utils.expansion

import kotlinx.datetime.*

fun Float.toDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(times(1000).toLong())
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
}

fun Float.toDate(): String {
    val instant = Instant.fromEpochMilliseconds(times(1000).toLong())
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
}

fun LocalDateTime.Companion.now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())