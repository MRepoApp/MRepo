package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.mrepo.database.entity.LocalModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDao {
    @Query("SELECT * FROM local")
    fun getAllAsFlow(): Flow<List<LocalModuleEntity>>

    @Query("SELECT * FROM local")
    fun getAll(): List<LocalModuleEntity>

    @Query("SELECT * FROM local WHERE id = :id LIMIT 1")
    suspend fun getByIdOrNull(id: String): LocalModuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: LocalModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<LocalModuleEntity>)

    @Delete
    suspend fun delete(list: List<LocalModuleEntity>)

    @Query("DELETE FROM local")
    suspend fun deleteAll()

    @Query("SELECT * FROM local_updatable")
    suspend fun getUpdatableTagAll(): List<LocalModuleEntity.Updatable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdatableTag(value: LocalModuleEntity.Updatable)

    @Query("SELECT * FROM local_updatable WHERE id = :id LIMIT 1")
    suspend fun hasUpdatableTagOrNull(id: String): LocalModuleEntity.Updatable?

    @Delete
    suspend fun deleteUpdatableTag(values: List<LocalModuleEntity.Updatable>)
}