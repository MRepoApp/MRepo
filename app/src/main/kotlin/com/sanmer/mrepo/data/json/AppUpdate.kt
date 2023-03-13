package com.sanmer.mrepo.data.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppUpdate(
    val version: String,
    val versionCode: Int,
    val apkUrl: String,
    val changelog: String
)