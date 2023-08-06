package com.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sanmer.mrepo.database.entity.Repo
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos")
    fun getAllAsFlow(): Flow<List<Repo>>

    @Query("SELECT * FROM repos WHERE enable = 1")
    fun getEnableAll(): List<Repo>

    @Query("SELECT * FROM repos WHERE url = :url LIMIT 1")
    fun getByUrl(url: String): Repo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(value: Repo)

    @Update
    suspend fun update(value: Repo)

    @Delete
    suspend fun delete(value: Repo)
}