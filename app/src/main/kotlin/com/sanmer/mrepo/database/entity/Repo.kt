package com.sanmer.mrepo.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sanmer.mrepo.model.online.ModulesJson

@Entity(tableName = "repos")
data class Repo(
    @PrimaryKey val url: String,
    val name: String = url,
    val enable: Boolean = true,
    @Embedded val metadata: RepoMetadata = RepoMetadata.default()
) {
    val isCompatible get() = metadata.version == ModulesJson.CURRENT_VERSION

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Repo -> url == other.url
            else -> false
        }
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    fun copy(modulesJson: ModulesJson) = copy(
        name = modulesJson.name,
        metadata = RepoMetadata(
            version = modulesJson.metadata.version,
            timestamp = modulesJson.metadata.timestamp,
            size = modulesJson.modules.size
        )
    )

    companion object {
        fun String.toRepo() = Repo(url = this)
    }
}

@Entity(tableName = "metadata")
data class RepoMetadata(
    val version: Int,
    val timestamp: Float,
    val size: Int
) {
    companion object {
        fun default() = RepoMetadata(
            version = ModulesJson.CURRENT_VERSION,
            timestamp = 0f,
            size = 0
        )
    }
}
