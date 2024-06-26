package dev.sanmer.mrepo.database.entity.online

import androidx.room.ColumnInfo
import androidx.room.Entity
import dev.sanmer.mrepo.model.online.VersionItem

@Entity(
    tableName = "version",
    primaryKeys = ["id", "repo_url", "version_code"]
)
data class VersionItemEntity(
    @ColumnInfo(name = "repo_url")
    val repoUrl: String,
    val id: String,
    val timestamp: Long,
    val version: String,
    @ColumnInfo(name = "version_code")
    val versionCode: Int,
    @ColumnInfo(name = "zip_url")
    val zipUrl: String,
    val changelog: String
) {
    constructor(
        repoUrl: String,
        id: String,
        original: VersionItem,
    ) : this(
        repoUrl = repoUrl,
        id = id,
        timestamp = original.timestamp,
        version = original.version,
        versionCode = original.versionCode,
        zipUrl = original.zipUrl,
        changelog = original.changelog
    )

    fun toJson() = VersionItem(
        repoUrl = repoUrl,
        timestamp = timestamp,
        version = version,
        versionCode = versionCode,
        zipUrl = zipUrl,
        changelog = changelog
    )
}