package com.sanmer.mrepo.data.database

import androidx.room.*

@Dao
interface LocalModuleDao {
    @Query("SELECT * FROM local_module")
    fun getAll(): List<LocalModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: LocalModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<LocalModuleEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(value: LocalModuleEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(list: List<LocalModuleEntity>)

    @Delete
    suspend fun delete(value: LocalModuleEntity)

    @Delete
    suspend fun delete(list: List<LocalModuleEntity>)

    @Query("DELETE FROM local_module")
    suspend fun deleteAll()
}