package dev.sanmer.mrepo.model.online

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VersionItem(
    @Json(ignore = true)
    val repoUrl: String = "",
    val timestamp: Long,
    val version: String,
    @Json(name = "version_code")
    val versionCode: Int,
    @Json(name = "zip_url")
    val zipUrl: String,
    val changelog: String
)