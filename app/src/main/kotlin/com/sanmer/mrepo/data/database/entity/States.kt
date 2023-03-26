package com.sanmer.mrepo.data.database.entity

import androidx.room.Entity
import com.sanmer.mrepo.data.module.States

@Entity(tableName = "states")
data class StatesEntity(
    val zipUrl: String,
    val changelog: String,
)

fun States.toEntity() = StatesEntity(
    zipUrl = zipUrl,
    changelog = changelog
)

fun StatesEntity.toStates() = States(
    zipUrl = zipUrl,
    changelog = changelog
)