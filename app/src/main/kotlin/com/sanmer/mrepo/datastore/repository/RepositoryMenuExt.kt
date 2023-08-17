package com.sanmer.mrepo.datastore.repository


data class RepositoryMenuExt(
    val option: Option,
    val pinInstalled: Boolean,
    val descending: Boolean,
    val showIcon: Boolean,
    val showLicense: Boolean,
    val showUpdatedTime: Boolean
) {
    companion object {
        fun default() = RepositoryMenuExt(
            option = Option.NAME,
            pinInstalled = false,
            descending = false,
            showIcon = true,
            showLicense = true,
            showUpdatedTime = true
        )
    }
}

fun RepositoryMenuExt.toProto(): RepositoryMenu = RepositoryMenu.newBuilder()
    .setOption(option)
    .setPinInstalled(pinInstalled)
    .setDescending(descending)
    .setShowIcon(showIcon)
    .setShowLicense(showLicense)
    .setShowUpdatedTime(showUpdatedTime)
    .build()

fun RepositoryMenu.toExt() = RepositoryMenuExt(
    option = option,
    pinInstalled = pinInstalled,
    descending = descending,
    showIcon = showIcon,
    showLicense = showLicense,
    showUpdatedTime = showUpdatedTime
)