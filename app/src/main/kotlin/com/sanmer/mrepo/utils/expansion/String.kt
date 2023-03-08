package com.sanmer.mrepo.utils.expansion

import java.io.File

fun String.toLongOr(v: Long): Long {
    if (isEmpty() || isBlank()) return v

    return try {
        toLong()
    } catch (e: NumberFormatException) {
        v
    }
}

fun String.toLongOrZero(): Long = toLongOr(0)

fun String.toFile(): File? {
    return try {
        File(this)
    } catch (e: Exception) {
        null
    }
}
