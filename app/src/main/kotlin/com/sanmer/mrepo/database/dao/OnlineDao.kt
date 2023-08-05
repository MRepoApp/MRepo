package com.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanmer.mrepo.database.entity.OnlineModuleEntity

@Dao
interface OnlineDao {
    @Query("SELECT * FROM onlineModules")
    fun getAll(): List<OnlineModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: OnlineModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<OnlineModuleEntity>)

    @Query("DELETE from onlineModules where repoUrl = :repoUrl")
    suspend fun deleteByUrl(repoUrl: String)

    @Query("DELETE FROM onlineModules")
    suspend fun deleteAll()
}