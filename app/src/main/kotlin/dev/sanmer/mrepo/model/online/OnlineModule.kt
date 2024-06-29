package dev.sanmer.mrepo.model.online

import dev.sanmer.mrepo.utils.StrUtil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnlineModule(
    val id: String,
    val name: String,
    val version: String,
    @SerialName("version_code")
    val versionCode: Int,
    val author: String,
    val description: String,
    val metadata: Metadata = Metadata(),
    val versions: List<VersionItem>,
) {
    val versionDisplay by lazy {
        StrUtil.getVersionDisplay(version, versionCode)
    }

    @Serializable
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