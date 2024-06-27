package dev.sanmer.mrepo.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class RepositoryMenu(
    val option: Option = Option.Name,
    val descending: Boolean = false,
    val pinInstalled: Boolean = true,
    val pinUpdatable: Boolean = true,
    val showIcon: Boolean = true,
    val showLicense: Boolean = true,
    val showUpdatedTime: Boolean = false
)
