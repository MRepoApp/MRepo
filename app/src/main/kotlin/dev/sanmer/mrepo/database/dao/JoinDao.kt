package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sanmer.mrepo.database.entity.LocalModuleEntity
import dev.sanmer.mrepo.database.entity.OnlineModuleEntity
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.database.entity.VersionItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JoinDao {
    @Query(
        "SELECT * FROM local " +
        "LEFT JOIN local_updatable ON local_updatable.id = local.id "
    )
    fun getLocalWithUpdatableAll(): Flow<Map<LocalModuleEntity, LocalModuleEntity.Updatable?>>

    @Query(
        "SELECT * FROM local " +
        "LEFT JOIN local_updatable ON local_updatable.id = local.id " +
        "WHERE local.id = :id"
    )
    suspend fun getLocalWithUpdatable(id: String): Map<LocalModuleEntity, LocalModuleEntity.Updatable?>

    @Query(
        "SELECT online.*, version.* FROM repo " +
        "INNER JOIN online ON online.repo_url = repo.url " +
        "INNER JOIN version ON version.id = online.id " +
        "WHERE repo.disable = 0 "
    )
    fun getOnlineAllAsFlow(): Flow<Map<OnlineModuleEntity, List<VersionItemEntity>>>

    @Query(
        "SELECT * FROM repo " +
        "INNER JOIN version ON version.repo_url = repo.url " +
        "WHERE repo.disable = 0 AND version.id = :id "
    )
    suspend fun getVersionWithRepo(id: String): Map<RepoEntity, List<VersionItemEntity>>
}