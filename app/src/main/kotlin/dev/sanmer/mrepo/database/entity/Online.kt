package dev.sanmer.mrepo.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.model.online.TrackJson

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
) {
    constructor(
        original: OnlineModule,
        repoUrl: String
    ) : this(
        id = original.id,
        repoUrl = repoUrl,
        name = original.name,
        version = original.version,
        versionCode = original.versionCode,
        author = original.author,
        description = original.description,
        track = TrackJsonEntity(original.track)
    )

    fun toModule() = OnlineModule(
        id = id,
        name = name,
        version = version,
        versionCode = versionCode,
        author = author,
        description = description,
        track = track.toTrack(),
        versions = listOf()
    )
}

@Entity(tableName = "track")
data class TrackJsonEntity(
    val type: String,
    val added: Float,
    val license: String,
    val homepage: String,
    val source: String,
    val support: String,
    val donate: String
) {
    constructor(original: TrackJson) : this(
        type = original.type.name,
        added = original.added,
        license = original.license,
        homepage = original.homepage,
        source = original.source,
        support = original.support,
        donate = original.donate
    )

    fun toTrack() = TrackJson(
        typeName = type,
        added = added,
        license = license,
        homepage = homepage,
        source = source,
        support = support,
        donate = donate
    )
}