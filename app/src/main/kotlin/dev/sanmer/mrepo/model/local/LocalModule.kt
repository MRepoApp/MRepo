package dev.sanmer.mrepo.model.local

import dev.sanmer.mrepo.utils.Utils
import dev.sanmer.mrepo.content.Module

typealias LocalModule = Module

val Module.versionDisplay get() = Utils.getVersionDisplay(version, versionCode)