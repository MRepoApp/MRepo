package com.sanmer.mrepo.datastore.repository

data class RepositoryMenuCompat(
    val option: Option,
    val descending: Boolean,
    val pinInstalled: Boolean,
    val pinUpdatable: Boolean,
    val showIcon: Boolean,
    val showLicense: Boolean,
    val showUpdatedTime: Boolean
) {
    constructor(original: RepositoryMenu) : this(
        option = original.option,
        descending = original.descending,
        pinInstalled = original.pinInstalled,
        pinUpdatable = original.pinUpdatable,
        showIcon = original.showIcon,
        showLicense = original.showLicense,
        showUpdatedTime = original.showUpdatedTime
    )

    fun toProto(): RepositoryMenu = RepositoryMenu.newBuilder()
        .setOption(option)
        .setDescending(descending)
        .setPinInstalled(pinInstalled)
        .setPinUpdatable(pinUpdatable)
        .setShowIcon(showIcon)
        .setShowLicense(showLicense)
        .setShowUpdatedTime(showUpdatedTime)
        .build()

    companion object {
        fun default() = RepositoryMenuCompat(
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