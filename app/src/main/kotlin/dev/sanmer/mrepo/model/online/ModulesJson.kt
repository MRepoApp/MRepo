package dev.sanmer.mrepo.model.online

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModulesJson(
    val name: String,
    val metadata: Metadata = Metadata.default(),
    val modules: List<OnlineModule>
) {
    @JsonClass(generateAdapter = true)
    data class Metadata(
        val timestamp: Float
    ) {
        companion object {
            fun default() = Metadata(
                timestamp = 0f
            )
        }
    }
}