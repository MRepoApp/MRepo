package com.sanmer.mrepo.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repo")
data class Repo(
    @PrimaryKey val url: String,
    val name: String = url,
    val enable: Boolean = true,
    @Embedded val metadata: RepoMetadata = RepoMetadata.default
) {
    constructor(
        url: String,
        name: String,
        enable: Boolean,
        size: Int,
        timestamp: Float,
        version: String,
        versionCode: Int
    ) : this(
        url = url,
        name = name,
        enable = enable,
        metadata = RepoMetadata(
            size = size,
            timestamp = timestamp,
            version = version,
            versionCode = versionCode
        )
    )

    fun isCompatible() = metadata.versionCode >= RepoMetadata.current.versionCode

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Repo -> url == other.url
            else -> false
        }
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    fun copy(
        url: String = this.url,
        name: String = this.name,
        enable: Boolean = this.enable,
        size: Int = this.metadata.size,
        timestamp: Float = this.metadata.timestamp,
        version: String = this.metadata.version,
        versionCode: Int = this.metadata.versionCode
    ) = copy(
        url = url,
        name = name,
        enable = enable,
        metadata = RepoMetadata(
            size = size,
            timestamp = timestamp,
            version = version,
            versionCode = versionCode
        )
    )
}

fun String.toRepo() = Repo(url = this)

@Entity(tableName = "metadata")
data class RepoMetadata(
    val size: Int,
    val timestamp: Float,
    val version: String,
    @ColumnInfo(name = "version_code") val versionCode: Int
) {
    companion object {
        val default = RepoMetadata(
            size = 0,
            timestamp = 0f,
            version = "1.0.0",
            versionCode = 240
        )

        val current = default
    }
}
