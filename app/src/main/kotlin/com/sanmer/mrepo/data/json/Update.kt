package com.sanmer.mrepo.data.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Update(
    val timestamp: Float,
    val versions: List<UpdateItem>,
    val repoId: Long = 0
)

@JsonClass(generateAdapter = true)
data class UpdateItem(
    val timestamp: Float,
    val version: String,
    val versionCode: Int,
    val zipUrl: String,
    val changelog: String,
    val repoId: Long = 0
)

val UpdateItem.versionDisplay get() = if ("(${versionCode})" in version) {
    version
} else {
    "$version (${versionCode})"
}
