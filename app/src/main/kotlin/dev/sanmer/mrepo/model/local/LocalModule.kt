package dev.sanmer.mrepo.model.local

import dev.sanmer.mrepo.content.Module
import dev.sanmer.mrepo.utils.StrUtil

typealias LocalModule = Module

val Module.versionDisplay get() = StrUtil.getVersionDisplay(version, versionCode)