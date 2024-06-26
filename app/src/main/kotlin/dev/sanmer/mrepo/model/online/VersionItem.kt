package dev.sanmer.mrepo.model.online

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class VersionItem(
    @Transient
    val repoUrl: String = "",
    val timestamp: Long,
    val version: String,
    @SerialName("version_code")
    val versionCode: Int,
    @SerialName("zip_url")
    val zipUrl: String,
    val changelog: String
)