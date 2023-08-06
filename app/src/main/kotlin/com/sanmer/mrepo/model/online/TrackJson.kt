package com.sanmer.mrepo.model.online

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackJson(
    @Json(name = "type") val typeName: String,
    val added: Float,
    val license: String = "",
    val homepage: String = "",
    val source: String = "",
    val support: String = "",
    val donate: String = ""
) {
    val type = TrackType.valueOf(typeName)
}

enum class TrackType {
    UNKNOWN,
    ONLINE_JSON,
    ONLINE_ZIP,
    GIT,
    LOCAL_JSON,
    LOCAL_ZIP,
}