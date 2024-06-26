package dev.sanmer.mrepo.model.json

import kotlinx.serialization.Serializable

@Serializable
data class License(
    val licenseText: String,
    val name: String,
    val licenseId: String,
    val seeAlso: List<String>,
    val isOsiApproved: Boolean,
    val isFsfLibre: Boolean = false,
) {
    val hasLabel by lazy {
        isFsfLibre || isOsiApproved
    }
}
