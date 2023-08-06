package com.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanmer.mrepo.database.entity.LocalModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDao {
    @Query("SELECT * FROM localModules")
    fun getAllAsFlow(): Flow<List<LocalModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: LocalModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<LocalModuleEntity>)

    @Query("DELETE FROM localModules")
    suspend fun deleteAll()
}