package com.sanmer.mrepo.model.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateJson(
    val timestamp: Float,
    val versions: List<VersionItem>,
    @Json(ignore = true) val repoUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class VersionItem(
    val timestamp: Float,
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String,
    @Json(ignore = true) val repoUrl: String = ""
)

val VersionItem.versionDisplay get() = if ("(${versionCode})" in version) {
    version
} else {
    "$version (${versionCode})"
}
