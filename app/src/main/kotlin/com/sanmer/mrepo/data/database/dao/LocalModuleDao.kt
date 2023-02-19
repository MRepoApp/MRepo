package com.sanmer.mrepo.data.database.dao

import androidx.room.*
import com.sanmer.mrepo.data.database.entity.LocalModuleEntity

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

    @Delete
    suspend fun delete(value: LocalModuleEntity)

    @Delete
    suspend fun delete(list: List<LocalModuleEntity>)

    @Query("DELETE FROM local_module")
    suspend fun deleteAll()
}