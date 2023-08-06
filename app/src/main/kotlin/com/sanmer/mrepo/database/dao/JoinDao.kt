package com.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.VersionItemEntity
import com.sanmer.mrepo.model.online.ModulesJson
import kotlinx.coroutines.flow.Flow

@Dao
interface JoinDao {
    @Query(
        "SELECT m.* " +
        "FROM onlineModules m " +
        "JOIN repos r ON m.repoUrl = r.url " +
        "WHERE r.enable = 1 AND r.version = :version"
    )
    fun getOnlineAllAsFlow(version: Int = ModulesJson.CURRENT_VERSION): Flow<List<OnlineModuleEntity>>

    @Query(
        "SELECT v.* " +
        "FROM versions v " +
        "JOIN repos r ON v.repoUrl = r.url " +
        "WHERE v.id = :id AND r.enable = 1 AND r.version = :version"
    )
    fun getVersionById(id: String, version: Int = ModulesJson.CURRENT_VERSION): List<VersionItemEntity>
}