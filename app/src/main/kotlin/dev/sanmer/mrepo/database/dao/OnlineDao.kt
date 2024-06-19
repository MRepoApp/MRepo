package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.mrepo.database.entity.OnlineModuleEntity

@Dao
interface OnlineDao {
    @Query("SELECT * FROM online WHERE id = :id")
    suspend fun getAllById(id: String): List<OnlineModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<OnlineModuleEntity>)

    @Query("DELETE FROM online WHERE repo_url = :repoUrl")
    suspend fun deleteByUrl(repoUrl: String)
}