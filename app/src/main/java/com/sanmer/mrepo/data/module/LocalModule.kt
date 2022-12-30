package com.sanmer.mrepo.data.module

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LocalModule(
    id: String = "" ,
    name: String = "",
    version: String = "",
    versionCode: Int = -1,
    author: String = "",
    description: String = ""
) : Module(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
) {
    var state by mutableStateOf(State.DISABLE)

    override fun toString(): String {
        return "LocalModule(id=$id, state=$state)"
    }
}