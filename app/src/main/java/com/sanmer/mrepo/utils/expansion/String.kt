package com.sanmer.mrepo.utils.expansion

import timber.log.Timber

fun String.toLongOr(v: Long): Long {
    if (isEmpty() || isBlank()) return v

    return try {
        toLong()
    } catch (e: NumberFormatException) {
        Timber.e(e.message)
        v
    }
}

fun String.toLongOrZero(): Long = toLongOr(0)
