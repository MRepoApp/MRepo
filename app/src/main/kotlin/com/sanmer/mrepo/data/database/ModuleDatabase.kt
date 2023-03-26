package com.sanmer.mrepo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanmer.mrepo.data.database.dao.ModuleDao
import com.sanmer.mrepo.data.database.entity.LocalModuleEntity

@Database(entities = [LocalModuleEntity::class], version = 1)
abstract class ModuleDatabase : RoomDatabase() {
    abstract fun moduleDao(): ModuleDao

    companion object {
        private var instance: ModuleDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): ModuleDatabase {
            instance?.let {
                return it
            }

            return Room.databaseBuilder(context.applicationContext,
                ModuleDatabase::class.java, "module")
                .build().apply {
                    renameTo(context, "mrepo", "module")
                    instance = this
                }
        }

        @Suppress("SameParameterValue")
        private fun renameTo(context: Context, old: String, new: String) {
            context.databaseList().forEach {
                if (it.startsWith(old)) {
                    val oldFile = context.getDatabasePath(it)
                    val newFile = context.getDatabasePath(it!!.replace(old, new))
                    oldFile.renameTo(newFile)
                }
            }
        }
    }
}