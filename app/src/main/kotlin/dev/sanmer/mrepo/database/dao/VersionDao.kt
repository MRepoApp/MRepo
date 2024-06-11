package dev.sanmer.mrepo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.mrepo.database.entity.VersionItemEntity

@Dao
interface VersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<VersionItemEntity>)

    @Query("DELETE FROM version WHERE repoUrl = :repoUrl")
    suspend fun deleteByUrl(repoUrl: String)
}