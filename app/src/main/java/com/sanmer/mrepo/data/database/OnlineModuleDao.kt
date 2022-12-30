package com.sanmer.mrepo.data.database

import androidx.room.*

@Dao
interface OnlineModuleDao {
    @Query("SELECT * FROM online_module")
    fun getAll(): List<OnlineModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: OnlineModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<OnlineModuleEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(value: OnlineModuleEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(list: List<OnlineModuleEntity>)

    @Delete
    suspend fun delete(value: OnlineModuleEntity)

    @Delete
    suspend fun delete(list: List<OnlineModuleEntity>)

    @Query("DELETE FROM online_module")
    suspend fun deleteAll()
}