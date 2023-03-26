package com.sanmer.mrepo.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State

@Entity(tableName = "local_module")
data class LocalModuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: String,
    @ColumnInfo(name = "version_code") val versionCode: Int,
    val author: String,
    val description: String,
    val state: Int
)

fun LocalModule.toEntity() = LocalModuleEntity(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
    state = state.toInt()
)

fun LocalModuleEntity.toModule() = LocalModule(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description
).let {
    it.state = state.toState()
    return@let it
}

private fun State.toInt() = when (this) {
    State.ENABLE -> 0
    State.REMOVE -> 1
    State.DISABLE -> 2
    State.UPDATE -> 3
    State.RIRU_DISABLE -> 4
    State.ZYGISK_DISABLE -> 5
    State.ZYGISK_UNLOADED -> 6
}

private fun Int.toState() = when (this) {
    0 -> State.ENABLE
    1 -> State.REMOVE
    2 -> State.DISABLE
    3 -> State.UPDATE
    4 -> State.RIRU_DISABLE
    5 -> State.ZYGISK_DISABLE
    6 -> State.ZYGISK_UNLOADED
    else -> State.DISABLE
}