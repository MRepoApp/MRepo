package dev.sanmer.mrepo.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class ModulesMenu(
    val option: Option = Option.Name,
    val descending: Boolean = false,
    val pinEnabled: Boolean = false,
    val showUpdatedTime: Boolean = false
)