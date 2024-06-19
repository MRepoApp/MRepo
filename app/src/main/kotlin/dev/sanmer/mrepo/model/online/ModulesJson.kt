package dev.sanmer.mrepo.model.online

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModulesJson(
    val name: String,
    val timestamp: Long,
    val metadata: Metadata = Metadata(),
    val modules: List<OnlineModule>
) {
    @JsonClass(generateAdapter = true)
    data class Metadata(
        val homepage: String = "",
        val donate: String = "",
        val support: String = ""
    )
}