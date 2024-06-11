package dev.sanmer.mrepo.model.online

import dev.sanmer.mrepo.utils.Utils
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
    val versionDisplay get() = Utils.getVersionDisplay(version, versionCode)
}