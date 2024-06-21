package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sanmer.mrepo.database.entity.online.OnlineModuleEntity
import dev.sanmer.mrepo.database.entity.online.RepoEntity
import dev.sanmer.mrepo.database.entity.online.VersionItemEntity
import dev.sanmer.mrepo.model.online.ModulesJson
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(value: RepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnline(list: List<OnlineModuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(list: List<VersionItemEntity>)

    @Query("DELETE FROM repo WHERE url = :url")
    suspend fun deleteRepoByUrl(url: String)

    @Query("DELETE FROM online WHERE repo_url = :repoUrl")
    suspend fun deleteOnlineByUrl(repoUrl: String)

    @Query("DELETE FROM online WHERE repo_url = :repoUrl AND id NOT IN (:list)")
    suspend fun deleteOnlineNotIn(repoUrl: String, list: List<String>)

    @Query("DELETE FROM version WHERE repo_url = :repoUrl")
    suspend fun deleteVersionByUrl(repoUrl: String)

    @Query("DELETE FROM version WHERE repo_url = :repoUrl AND id NOT IN (:list)")
    suspend fun deleteVersionNotIn(repoUrl: String, list: List<String>)

    @Transaction
    suspend fun updateRepo(repo: RepoEntity, modulesJson: ModulesJson) {
        val moduleIds = modulesJson.modules.map { it.id }

        insertRepo(repo.copy(modulesJson))

        deleteOnlineNotIn(repo.url, moduleIds)
        insertOnline(
            modulesJson.modules.map {
                OnlineModuleEntity(repo.url, it)
            }
        )

        deleteVersionNotIn(repo.url, moduleIds)
        insertVersion(
            modulesJson.modules.map { module ->
                module.versions.map {
                    VersionItemEntity(repo.url, module.id, it)
                }
            }.flatten()
        )
    }

    @Transaction
    suspend fun deleteRepo(url: String) {
        deleteRepoByUrl(url)
        deleteOnlineByUrl(url)
        deleteVersionByUrl(url)
    }

    @Query("SELECT * FROM repo")
    fun getRepoAllAsFlow(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repo")
    suspend fun getRepoAll(): List<RepoEntity>

    @Query(
        "SELECT online.*, version.* FROM repo " +
        "INNER JOIN online ON online.repo_url = repo.url " +
        "INNER JOIN version ON version.id = online.id " +
        "WHERE repo.disable = 0 "
    )
    fun getOnlineAndVersionAllAsFlow(): Flow<Map<OnlineModuleEntity, List<VersionItemEntity>>>

    @Query("SELECT * FROM online WHERE id = :id")
    suspend fun getOnlineById(id: String): List<OnlineModuleEntity>

    @Query(
        "SELECT * FROM repo " +
        "INNER JOIN version ON version.repo_url = repo.url " +
        "WHERE repo.disable = 0 AND version.id = :id "
    )
    suspend fun getVersionAndRepoById(id: String): Map<RepoEntity, List<VersionItemEntity>>
}