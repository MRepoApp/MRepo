package com.sanmer.mrepo.data.module

open class Module(
    var id: String,
    var name: String,
    var version: String,
    var versionCode: Int,
    var author: String,
    var description: String
) {
    operator fun compareTo(other: Module) = id.compareTo(other.id)
}