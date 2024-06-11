package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.mrepo.database.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repo")
    fun getAllAsFlow(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repo")
    suspend fun getAll(): List<RepoEntity>

    @Query("SELECT * FROM repo WHERE url = :url LIMIT 1")
    suspend fun getByUrl(url: String): RepoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: RepoEntity)

    @Delete
    suspend fun delete(value: RepoEntity)
}