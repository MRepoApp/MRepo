package dev.sanmer.mrepo.model.online

import kotlinx.serialization.Serializable

@Serializable
data class ModulesJson(
    val name: String,
    val timestamp: Long,
    val metadata: Metadata = Metadata(),
    val modules: List<OnlineModule>
) {
    @Serializable
    data class Metadata(
        val homepage: String = "",
        val donate: String = "",
        val support: String = ""
    )
}