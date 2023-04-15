package com.sanmer.mrepo.database.dao

import androidx.room.*
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.RepoWithModule
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Transaction
    @Query("SELECT * FROM repo")
    fun getRepoWithModule(): List<RepoWithModule>

    @Transaction
    @Query("SELECT * FROM repo")
    fun getRepoWithModuleAsFlow(): Flow<List<RepoWithModule>>

    @Query("SELECT * FROM repo")
    fun getRepoAll(): List<Repo>

    @Query("SELECT * FROM repo")
    fun getRepoAllAsFlow(): Flow<List<Repo>>

    @Query("SELECT COUNT(url) FROM repo")
    fun getRepoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM repo WHERE enable LIKE 1")
    fun getEnableCount(): Flow<Int>

    @Query("SELECT * FROM repo WHERE url LIKE :url LIMIT 1")
    fun getRepoByUrl(url: String): Repo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(value: Repo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRepo(value: Repo)

    @Delete
    suspend fun deleteRepo(value: Repo)

    @Query("SELECT * FROM online_module")
    fun getModuleAll(): List<OnlineModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(value: OnlineModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(list: List<OnlineModuleEntity>)

    @Query("DELETE from online_module where repo_url = :repoUrl")
    suspend fun deleteModuleByUrl(repoUrl: String)

    @Query("DELETE FROM online_module")
    suspend fun deleteModuleAll()
}