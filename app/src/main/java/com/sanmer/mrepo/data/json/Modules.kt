package com.sanmer.mrepo.data.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Modules(
    val name: String,
    val timestamp: Float,
    val modules: List<OnlineModule>,
    @Json(ignore = true) val repoId: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Modules -> repoId == other.repoId
            else -> false
        }
    }

    override fun hashCode(): Int {
        return repoId.hashCode()
    }
}