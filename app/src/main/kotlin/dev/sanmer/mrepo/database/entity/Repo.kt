package dev.sanmer.mrepo.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.sanmer.mrepo.model.online.ModulesJson

@Entity(tableName = "repo")
data class RepoEntity(
    @PrimaryKey val url: String,
    val name: String = url,
    val enable: Boolean = true,
    @Embedded val metadata: Metadata = Metadata.default()
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is RepoEntity -> url == other.url
            else -> false
        }
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    fun copy(modulesJson: ModulesJson) = copy(
        name = modulesJson.name,
        metadata = Metadata(
            timestamp = modulesJson.metadata.timestamp,
            size = modulesJson.modules.size
        )
    )

    data class Metadata(
        val timestamp: Float,
        val size: Int
    ) {
        companion object {
            fun default() = Metadata(
                timestamp = 0f,
                size = 0
            )
        }
    }

    companion object {
        fun String.toRepo() = RepoEntity(url = this)
    }
}
