package com.sanmer.mrepo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalModuleEntity::class,
        OnlineModuleEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localModuleDao(): LocalModuleDao
    abstract fun onlineModuleDao(): OnlineModuleDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }

            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "mrepo")
                .build().apply {
                    instance = this
                }
        }
    }
}