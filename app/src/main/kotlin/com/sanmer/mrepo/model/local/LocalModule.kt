package com.sanmer.mrepo.model.local

import com.sanmer.mrepo.utils.Utils
import dev.sanmer.mrepo.compat.content.LocalModule

typealias LocalModule = LocalModule

val LocalModule.versionDisplay get() = Utils.getVersionDisplay(version, versionCode)

fun LocalModule.Companion.example() =
    LocalModule(
        id = "local_example",
        name = "Example",
        version = "2022.08.16",
        versionCode = 1703,
        author = "Sanmer",
        description = "This is an example!",
        updateJson = "",
        state = State.ENABLE,
        lastUpdated = 0L
    )