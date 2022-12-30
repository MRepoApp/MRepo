package com.sanmer.mrepo.data.json

import com.sanmer.mrepo.data.module.OnlineModule

data class Modules(
    val timestamp: String,
    val modules: List<OnlineModule>
)