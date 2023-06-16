package com.sanmer.mrepo.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RepoWithModule(
    @Embedded val repo: Repo,
    @Relation(
        parentColumn = "url",
        entityColumn = "repo_url",
        entity = OnlineModuleEntity::class
    )
    val modules: List<OnlineModuleEntity> = listOf()
)