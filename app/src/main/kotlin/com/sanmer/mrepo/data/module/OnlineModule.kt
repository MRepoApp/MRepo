package com.sanmer.mrepo.data.module

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnlineModule(
    val id: String = "unknown",
    val name: String = id,
    val version: String = id,
    val versionCode: Int = -1,
    val author: String = id,
    val description: String = id,
    val license: String = id,
    val states: States = States(),
    @Json(ignore = true) val repoUrls: MutableList<String> = mutableListOf()
) {
    val repoUrl get() = try {
        repoUrls.first()
    } catch (e: NoSuchElementException) {
        ""
    }

    val versionDisplay get() = if ("(${versionCode})" in version) {
        version
    } else {
        "$version (${versionCode})"
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is LocalModule -> id == other.id
            is OnlineModule -> id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@JsonClass(generateAdapter = true)
data class States(
    val zipUrl: String = "",
    val changelog: String = "",
)