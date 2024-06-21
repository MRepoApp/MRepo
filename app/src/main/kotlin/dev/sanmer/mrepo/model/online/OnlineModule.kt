package dev.sanmer.mrepo.model.online

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.sanmer.mrepo.utils.Utils

@JsonClass(generateAdapter = true)
data class OnlineModule(
    val id: String,
    val name: String,
    val version: String,
    @Json(name = "version_code")
    val versionCode: Int,
    val author: String,
    val description: String,
    val metadata: Metadata = Metadata(),
    val versions: List<VersionItem>,
) {
    val versionDisplay by lazy {
        Utils.getVersionDisplay(version, versionCode)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is OnlineModule -> id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    @JsonClass(generateAdapter = true)
    data class Metadata(
        val license: String = "",
        val homepage: String = "",
        val source: String = "",
        val donate: String = "",
        val support: String = ""
    )

    companion object {
        fun example() = OnlineModule(
            id = "online_example",
            name = "Example",
            version = "2022.08.16",
            versionCode = 1703,
            author = "Sanmer",
            description = "This is an example!",
            metadata = Metadata(
                license = "GPL-3.0"
            ),
            versions = emptyList()
        )
    }
}