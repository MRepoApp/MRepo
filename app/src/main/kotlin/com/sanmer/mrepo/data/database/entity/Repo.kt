package com.sanmer.mrepo.data.database.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.*
import kotlin.reflect.KProperty

@Entity(tableName = "repo")
data class Repo(
    @PrimaryKey val url: String,
    val name: String = url,
    val size: Int = 0,
    val timestamp: Float = 0f,
    var enable: Boolean = true
) {
    @get:Ignore
    @set:JvmName("state")
    var isEnable: Boolean by mutableStateOf(enable)

    private operator fun MutableState<Boolean>.setValue(
        thisObj: Any?,
        property: KProperty<*>,
        value: Boolean
    ) {
        this.value = value
        enable = value
    }

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