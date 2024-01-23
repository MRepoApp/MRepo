package com.sanmer.mrepo.model.state

import androidx.compose.runtime.Immutable
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.RepoMetadata

@Immutable
data class RepoState(
    val url: String,
    val name: String,
    val enable: Boolean,
    val compatible: Boolean,
    val version: Int,
    val timestamp: Float,
    val size: Int
) {
    constructor(repo: Repo) : this(
        url = repo.url,
        name = repo.name,
        enable = repo.enable,
        compatible = repo.isCompatible,
        version = repo.metadata.version,
        timestamp = repo.metadata.timestamp,
        size = repo.metadata.size
    )

    fun toRepo() = Repo(
        url = url,
        name = name,
        enable = enable,
        metadata = RepoMetadata(
            version = version,
            timestamp = timestamp,
            size = size
        )
    )
}