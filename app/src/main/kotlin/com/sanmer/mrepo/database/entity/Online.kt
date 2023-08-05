package com.sanmer.mrepo.database.entity

import androidx.room.Entity
import com.sanmer.mrepo.model.module.OnlineModule

@Entity(tableName = "onlineModules", primaryKeys = ["id", "repoUrl"])
data class OnlineModuleEntity(
    val id: String,
    val repoUrl: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val license: String
)

fun OnlineModuleEntity.toModule() = OnlineModule(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
    license = license,
    repoUrls = mutableListOf(repoUrl)
)

fun OnlineModule.toEntity(repoUrl: String) = OnlineModuleEntity(
    id = id,
    repoUrl = repoUrl,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
    license = license
)