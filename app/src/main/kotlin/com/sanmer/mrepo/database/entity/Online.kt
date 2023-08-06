package com.sanmer.mrepo.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.TrackJson

@Entity(tableName = "onlineModules", primaryKeys = ["id", "repoUrl"])
data class OnlineModuleEntity(
    val id: String,
    val repoUrl: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    @Embedded val track: TrackJsonEntity
)

fun OnlineModule.toEntity(repoUrl: String) = OnlineModuleEntity(
    id = id,
    repoUrl = repoUrl,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
    track = TrackJsonEntity(
        type = track.type.name,
        added = track.added,
        license = track.license,
        homepage = track.homepage,
        source = track.source,
        support = track.support,
        donate = track.donate
    )
)

fun OnlineModuleEntity.toModule() = OnlineModule(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
    track = TrackJson(
        typeName = track.type,
        added = track.added,
        license = track.license,
        homepage = track.homepage,
        source = track.source,
        support = track.support,
        donate = track.donate
    ),
    versions = listOf()
)

@Entity(tableName = "track")
data class TrackJsonEntity(
    val type: String,
    val added: Float,
    val license: String,
    val homepage: String,
    val source: String,
    val support: String,
    val donate: String
)