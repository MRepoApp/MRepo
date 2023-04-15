package com.sanmer.mrepo.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "repo")
data class Repo(
    @PrimaryKey val url: String,
    val name: String = url,
    val size: Int = 0,
    val timestamp: Float = 0f,
    var enable: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Repo -> url == other.url
            else -> false
        }
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}

data class RepoWithModule(
    @Embedded val repo: Repo,
    @Relation(
        parentColumn = "url",
        entityColumn = "repo_url",
        entity = OnlineModuleEntity::class
    )
    val modules: List<OnlineModuleEntity> = listOf()
)

fun String.toRepo() = Repo(url = this)