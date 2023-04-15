package com.sanmer.mrepo.model.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppUpdate(
    val version: String,
    val versionCode: Int,
    val apkUrl: String,
    val changelog: String
) {
    companion object {
        fun empty() = AppUpdate(
            version = "1",
            versionCode = 1,
            apkUrl = "",
            changelog = ""
        )
    }
}