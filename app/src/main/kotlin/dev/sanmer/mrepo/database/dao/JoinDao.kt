package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sanmer.mrepo.database.entity.OnlineModuleEntity
import dev.sanmer.mrepo.database.entity.VersionItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JoinDao {
    @Query(
        "SELECT * " +
        "FROM online JOIN repo " +
        "ON online.repoUrl = repo.url " +
        "WHERE repo.enable = 1"
    )
    fun getOnlineAllAsFlow(): Flow<List<OnlineModuleEntity>>

    @Query(
        "SELECT online.* " +
        "FROM online JOIN repo " +
        "ON online.repoUrl = repo.url " +
        "WHERE online.id = :id AND online.repoUrl = :repoUrl AND repo.enable = 1 LIMIT 1"
    )
    suspend fun getOnlineByIdAndUrl(id: String, repoUrl: String): OnlineModuleEntity

    @Query(
        "SELECT version.* " +
        "FROM version JOIN repo " +
        "ON version.repoUrl = repo.url " +
        "WHERE version.id = :id AND repo.enable = 1 ORDER BY version.versionCode DESC"
    )
    suspend fun getVersionById(id: String): List<VersionItemEntity>
}