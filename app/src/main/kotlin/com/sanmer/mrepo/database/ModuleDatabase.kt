package com.sanmer.mrepo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sanmer.mrepo.database.dao.ModuleDao
import com.sanmer.mrepo.database.entity.LocalModuleEntity

@Database(entities = [LocalModuleEntity::class], version = 1)
abstract class ModuleDatabase : RoomDatabase() {
    abstract fun moduleDao(): ModuleDao
}