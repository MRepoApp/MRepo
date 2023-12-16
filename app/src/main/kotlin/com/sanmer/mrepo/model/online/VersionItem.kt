package com.sanmer.mrepo.model.online

import com.sanmer.mrepo.utils.ModuleUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VersionItem(
    @Json(ignore = true) val repoUrl: String = "",
    val timestamp: Float,
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String
) {
    val versionDisplay get() = ModuleUtils.getVersionDisplay(version, versionCode)
}