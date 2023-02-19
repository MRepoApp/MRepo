package com.sanmer.mrepo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanmer.mrepo.data.database.dao.RepoDao
import com.sanmer.mrepo.data.database.entity.Repo

@Database(entities = [Repo::class], version = 1)
abstract class RepoDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao

    companion object {
        private var instance: RepoDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): RepoDatabase {
            instance?.let {
                return it
            }

            return Room.databaseBuilder(context.applicationContext,
                RepoDatabase::class.java, "repo")
                .build().apply {
                    instance = this
                }
        }
    }
}