package com.sanmer.mrepo.model.online

import com.sanmer.mrepo.utils.Utils
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
    val versionDisplay get() = Utils.getVersionDisplay(version, versionCode)

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is OnlineModule -> id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun example() = OnlineModule(
            id = "online_example",
            name = "Example",
            version = "2022.08.16",
            versionCode = 1703,
            author = "Sanmer",
            description = "This is an example!",
            track = TrackJson(
                typeName = "ONLINE_JSON",
                added = 0f,
                license = "GPL-3.0"
            ),
            versions = emptyList()
        )
    }
}