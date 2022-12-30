package com.sanmer.mrepo.data.module

class OnlineModule(
    var states: States = States(),
    var update: String = "",
    id: String = "",
    name: String = "",
    version: String = "",
    versionCode: Int = -1,
    author: String = "",
    description: String = "",
) : Module(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
) {

    override fun toString(): String {
        return "OnlineModule(id=$id, status=$states)"
    }
}
