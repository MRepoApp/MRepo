package com.sanmer.mrepo.model.local

import com.sanmer.mrepo.utils.Utils
import dev.sanmer.mrepo.content.Module

typealias LocalModule = Module

val Module.versionDisplay get() = Utils.getVersionDisplay(version, versionCode)