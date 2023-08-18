package com.sanmer.mrepo.datastore.repository


data class RepositoryMenuExt(
    val option: Option,
    val descending: Boolean,
    val pinInstalled: Boolean,
    val pinUpdatable: Boolean,
    val showIcon: Boolean,
    val showLicense: Boolean,
    val showUpdatedTime: Boolean
) {
    companion object {
        fun default() = RepositoryMenuExt(
            option = Option.NAME,
            descending = false,
            pinInstalled = false,
            pinUpdatable = true,
            showIcon = true,
            showLicense = true,
            showUpdatedTime = true
        )
    }
}

fun RepositoryMenuExt.toProto(): RepositoryMenu = RepositoryMenu.newBuilder()
    .setOption(option)
    .setDescending(descending)
    .setPinInstalled(pinInstalled)
    .setPinUpdatable(pinUpdatable)
    .setShowIcon(showIcon)
    .setShowLicense(showLicense)
    .setShowUpdatedTime(showUpdatedTime)
    .build()

fun RepositoryMenu.toExt() = RepositoryMenuExt(
    option = option,
    descending = descending,
    pinInstalled = pinInstalled,
    pinUpdatable = pinUpdatable,
    showIcon = showIcon,
    showLicense = showLicense,
    showUpdatedTime = showUpdatedTime
)