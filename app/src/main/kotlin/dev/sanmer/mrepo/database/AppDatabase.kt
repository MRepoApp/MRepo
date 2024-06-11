package dev.sanmer.mrepo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sanmer.mrepo.database.dao.JoinDao
import dev.sanmer.mrepo.database.dao.LocalDao
import dev.sanmer.mrepo.database.dao.OnlineDao
import dev.sanmer.mrepo.database.dao.RepoDao
import dev.sanmer.mrepo.database.dao.VersionDao
import dev.sanmer.mrepo.database.entity.LocalModuleEntity
import dev.sanmer.mrepo.database.entity.OnlineModuleEntity
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.database.entity.VersionItemEntity

@Database(
    entities = [
        RepoEntity::class,
        LocalModuleEntity.Updatable::class,
        OnlineModuleEntity::class,
        VersionItemEntity::class,
        LocalModuleEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun onlineDao(): OnlineDao
    abstract fun versionDao(): VersionDao
    abstract fun localDao(): LocalDao
    abstract fun joinDao(): JoinDao

    companion object {
        fun build(context: Context) =
            Room.databaseBuilder(context,
                AppDatabase::class.java, "mrepo")
                .build()
    }
}