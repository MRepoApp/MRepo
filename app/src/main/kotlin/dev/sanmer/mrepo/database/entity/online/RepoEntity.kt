package dev.sanmer.mrepo.database.entity.online

import androidx.room.Embedded
import androidx.room.Entity
import dev.sanmer.mrepo.model.online.ModulesJson

@Entity(
    tableName = "repo",
    primaryKeys = ["url"]
)
data class RepoEntity(
    val url: String,
    val disable: Boolean,
    val size: Int,
    val name: String,
    val timestamp: Long,
    @Embedded val metadata: Metadata
) {
    constructor(url: String) : this(
        url = url,
        disable = false,
        size = 0,
        name = url,
        timestamp = 0L,
        metadata = Metadata()
    )

    fun copy(modulesJson: ModulesJson) = copy(
        size = modulesJson.modules.size,
        name = modulesJson.name,
        timestamp = modulesJson.timestamp,
        metadata = Metadata(modulesJson.metadata)
    )

    data class Metadata(
        val homepage: String = "",
        val donate: String = "",
        val support: String = ""
    ) {
        constructor(original: ModulesJson.Metadata) : this(
            homepage = original.homepage,
            donate = original.donate,
            support = original.support
        )
    }
}
