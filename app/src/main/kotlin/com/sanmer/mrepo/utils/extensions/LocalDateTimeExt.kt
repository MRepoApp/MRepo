package com.sanmer.mrepo.utils.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Float.toDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(times(1000).toLong())
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
}

fun Float.toDate(): String {
    val instant = Instant.fromEpochMilliseconds(times(1000).toLong())
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
}

fun Long.toDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
}

fun Long.toDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
}

fun LocalDateTime.Companion.now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())