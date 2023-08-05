package com.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.RepoWithModule
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Transaction
    @Query("SELECT * FROM repos")
    fun getWithModule(): List<RepoWithModule>

    @Transaction
    @Query("SELECT * FROM repos")
    fun getWithModuleAsFlow(): Flow<List<RepoWithModule>>

    @Query("SELECT * FROM repos")
    fun getAll(): List<Repo>

    @Query("SELECT * FROM repos")
    fun getAllAsFlow(): Flow<List<Repo>>

    @Query("SELECT * FROM repos WHERE url LIKE :url LIMIT 1")
    fun getByUrl(url: String): Repo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: Repo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(value: Repo)

    @Delete
    suspend fun delete(value: Repo)
}