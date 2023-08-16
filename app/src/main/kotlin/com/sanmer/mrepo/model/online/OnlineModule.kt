package com.sanmer.mrepo.model.online

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.utils.ModuleUtils.getVersionDisplay
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnlineModule(
    val id: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val track: TrackJson,
    val versions: List<VersionItem>,
) {
    val versionDisplay get() = getVersionDisplay(version, versionCode)

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