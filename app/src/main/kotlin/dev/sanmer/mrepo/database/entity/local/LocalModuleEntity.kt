package dev.sanmer.mrepo.database.entity.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.local.State

@Entity(
    tableName = "local",
    primaryKeys = ["id"]
)
data class LocalModuleEntity(
    val id: String,
    val name: String,
    val version: String,
    @ColumnInfo(name = "version_code")
    val versionCode: Int,
    val author: String,
    val description: String,
    val state: String,
    @ColumnInfo(name = "update_json")
    val updateJson: String,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long
) {
    constructor(original: LocalModule) : this(
        id = original.id,
        name = original.name,
        version = original.version,
        versionCode = original.versionCode,
        author = original.author,
        description = original.description,
        state = original.state.name,
        updateJson = original.updateJson,
        lastUpdated = original.lastUpdated
    )

    fun toModule() = LocalModule(
        id = id,
        name = name,
        version = version,
        versionCode = versionCode,
        author = author,
        description = description,
        updateJson = updateJson,
        state = State.valueOf(state),
        lastUpdated = lastUpdated
    )

    @Entity(
        tableName = "local_updatable",
        primaryKeys = ["id"]
    )
    data class Updatable(
        val id: String,
        val updatable: Boolean
    )
}