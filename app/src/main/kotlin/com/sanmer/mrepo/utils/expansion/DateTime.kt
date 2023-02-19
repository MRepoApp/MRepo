package com.sanmer.mrepo.utils.expansion

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Float.toDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = Date(times(1000).toLong())
    return dateFormat.format(date)
}

@SuppressLint("SimpleDateFormat")
fun Float.toDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = Date(times(1000).toLong())
    return dateFormat.format(date)
}