package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sanmer.mrepo.database.entity.local.LocalModuleEntity
import dev.sanmer.mrepo.model.local.LocalModule
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocal(value: LocalModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocal(list: List<LocalModuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdatable(value: LocalModuleEntity.Updatable)

    @Query("DELETE FROM local_updatable WHERE id NOT IN (:list)")
    suspend fun deleteUpdatableNotIn(list: List<String>)

    @Query("DELETE FROM local WHERE id NOT IN (:list)")
    suspend fun deleteLocalNotIn(list: List<String>)

    @Transaction
    suspend fun updateLocal(list: List<LocalModule>) {
        val moduleIds = list.map { it.id }

        deleteUpdatableNotIn(moduleIds)
        deleteLocalNotIn(moduleIds)
        insertLocal(list.map { LocalModuleEntity(it) })
    }

    @Query("SELECT * FROM local")
    fun getLocalAllAsFlow(): Flow<List<LocalModuleEntity>>

    @Query(
        "SELECT * FROM local " +
        "LEFT JOIN local_updatable ON local_updatable.id = local.id "
    )
    fun getLocalAndUpdatableAllAsFlow(): Flow<Map<LocalModuleEntity, LocalModuleEntity.Updatable?>>

    @Query(
        "SELECT * FROM local " +
        "LEFT JOIN local_updatable ON local_updatable.id = local.id " +
        "WHERE local.id = :id"
    )
    suspend fun getLocalAndUpdatableById(id: String): Map<LocalModuleEntity, LocalModuleEntity.Updatable?>

    @Query("SELECT * FROM local_updatable WHERE id = :id")
    suspend fun getUpdatable(id: String): LocalModuleEntity.Updatable?
}