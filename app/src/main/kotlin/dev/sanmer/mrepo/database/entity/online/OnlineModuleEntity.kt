package dev.sanmer.mrepo.database.entity.online

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.model.online.VersionItem

@Entity(
    tableName = "online",
    primaryKeys = ["id", "repo_url"]
)
data class OnlineModuleEntity(
    @ColumnInfo(name = "repo_url")
    val repoUrl: String,
    val id: String,
    val name: String,
    val version: String,
    @ColumnInfo(name = "version_code")
    val versionCode: Int,
    val author: String,
    val description: String,
    @Embedded val metadata: Metadata
) {
    constructor(
        repoUrl: String,
        original: OnlineModule
    ) : this(
        repoUrl = repoUrl,
        id = original.id,
        name = original.name,
        version = original.version,
        versionCode = original.versionCode,
        author = original.author,
        description = original.description,
        metadata = Metadata(original.metadata)
    )

    fun toJson(versions: List<VersionItem> = emptyList()) = OnlineModule(
        id = id,
        name = name,
        version = version,
        versionCode = versionCode,
        author = author,
        description = description,
        metadata = metadata.toJson(),
        versions = versions
    )

    data class Metadata(
        val license: String = "",
        val homepage: String = "",
        val source: String = "",
        val donate: String = "",
        val support: String = ""
    ) {
        constructor(original: OnlineModule.Metadata) : this(
            license = original.license,
            homepage = original.homepage,
            source = original.source,
            support = original.support,
            donate = original.donate
        )

        fun toJson() = OnlineModule.Metadata(
            license = license,
            homepage = homepage,
            source = source,
            support = support,
            donate = donate
        )
    }
}