package com.sanmer.mrepo.datastore.modules

import com.sanmer.mrepo.datastore.repository.Option

data class ModulesMenuExt(
    val option: Option,
    val descending: Boolean,
    val pinEnabled: Boolean,
    val showUpdatedTime: Boolean
) {
    companion object {
        fun default() = ModulesMenuExt(
            option = Option.NAME,
            descending = false,
            pinEnabled = false,
            showUpdatedTime = true
        )
    }
}

fun ModulesMenuExt.toProto(): ModulesMenu = ModulesMenu.newBuilder()
    .setOption(option)
    .setDescending(descending)
    .setPinEnabled(pinEnabled)
    .setShowUpdatedTime(showUpdatedTime)
    .build()

fun ModulesMenu.toExt() = ModulesMenuExt(
    option = option,
    descending = descending,
    pinEnabled = pinEnabled,
    showUpdatedTime = showUpdatedTime
)