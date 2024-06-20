package dev.sanmer.mrepo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sanmer.mrepo.database.dao.LocalDao
import dev.sanmer.mrepo.database.dao.RepoDao
import dev.sanmer.mrepo.database.entity.local.LocalModuleEntity
import dev.sanmer.mrepo.database.entity.online.OnlineModuleEntity
import dev.sanmer.mrepo.database.entity.online.RepoEntity
import dev.sanmer.mrepo.database.entity.online.VersionItemEntity

@Database(
    entities = [
        RepoEntity::class,
        OnlineModuleEntity::class,
        VersionItemEntity::class,
        LocalModuleEntity::class,
        LocalModuleEntity.Updatable::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun localDao(): LocalDao

    companion object {
        fun build(context: Context) =
            Room.databaseBuilder(context,
                AppDatabase::class.java, "mrepo")
                .build()
    }
}