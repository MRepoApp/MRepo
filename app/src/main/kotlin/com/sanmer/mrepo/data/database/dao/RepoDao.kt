package com.sanmer.mrepo.data.database.dao

import androidx.room.*
import com.sanmer.mrepo.data.database.entity.Repo

@Dao
interface RepoDao {
    @Query("SELECT * FROM repo")
    fun getAll(): List<Repo>

    @Query("SELECT * FROM repo WHERE id LIKE :id LIMIT 1")
    fun getById(id: Long): Repo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: Repo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(value: Repo)

    @Delete
    suspend fun delete(value: Repo)

    @Query("DELETE FROM repo")
    suspend fun deleteAll()
}