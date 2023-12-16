package com.sanmer.mrepo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.sanmer.mrepo.database.dao.JoinDao
import com.sanmer.mrepo.database.dao.LocalDao
import com.sanmer.mrepo.database.dao.OnlineDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.dao.VersionDao
import com.sanmer.mrepo.database.entity.LocalModuleEntity
import com.sanmer.mrepo.database.entity.LocalModuleUpdatable
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.VersionItemEntity

@Database(
    entities = [
        Repo::class,
        LocalModuleUpdatable::class,
        OnlineModuleEntity::class,
        VersionItemEntity::class,
        LocalModuleEntity::class
    ],
    version = 8
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun onlineDao(): OnlineDao
    abstract fun versionDao(): VersionDao
    abstract fun localDao(): LocalDao
    abstract fun joinDao(): JoinDao

    companion object {
        /**
         * Only migrate data for [Repo] and [LocalModuleUpdatable]
         */
        fun build(context: Context) =
            Room.databaseBuilder(context,
                AppDatabase::class.java, "mrepo")
                .addMigrations(
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8
                )
                .build()

        private val MIGRATION_3_4 = Migration(3, 4) {
            it.execSQL("CREATE TABLE IF NOT EXISTS localModules (" +
                    "id TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "versionCode INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "state TEXT NOT NULL, " +
                    "PRIMARY KEY(id))")

            it.execSQL("CREATE TABLE IF NOT EXISTS onlineModules (" +
                    "id TEXT NOT NULL, " +
                    "repoUrl TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "versionCode INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "license TEXT NOT NULL, " +
                    "PRIMARY KEY(id, repoUrl))")

            it.execSQL("DROP TABLE online_module")
            it.execSQL("ALTER TABLE repo RENAME TO repos")
        }

        private val MIGRATION_4_5 = Migration(4, 5) {
            it.execSQL("CREATE TABLE IF NOT EXISTS versions (" +
                "id TEXT NOT NULL, " +
                "repoUrl TEXT NOT NULL, " +
                "timestamp REAL NOT NULL, " +
                "version TEXT NOT NULL, " +
                "versionCode INTEGER NOT NULL, " +
                "zipUrl TEXT NOT NULL, " +
                "changelog TEXT NOT NULL, " +
                "PRIMARY KEY(id, repoUrl, versionCode))")

            it.execSQL("CREATE TABLE IF NOT EXISTS repos_new (" +
                    "url TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "enable INTEGER NOT NULL, " +
                    "version INTEGER NOT NULL, " +
                    "timestamp REAL NOT NULL, " +
                    "size INTEGER NOT NULL, " +
                    "PRIMARY KEY(url))")

            it.execSQL("INSERT INTO repos_new (" +
                    "url, name, enable, version, timestamp, size) " +
                    "SELECT " +
                    "url, name, enable, 0, timestamp, size " +
                    "FROM repos")

            it.execSQL("DROP TABLE repos")
            it.execSQL("ALTER TABLE repos_new RENAME TO repos")

            it.execSQL("CREATE TABLE IF NOT EXISTS onlineModules_new (" +
                    "id TEXT NOT NULL, " +
                    "repoUrl TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "versionCode INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "added REAL NOT NULL, " +
                    "license TEXT NOT NULL, " +
                    "homepage TEXT NOT NULL, " +
                    "source TEXT NOT NULL, " +
                    "support TEXT NOT NULL, " +
                    "donate TEXT NOT NULL, " +
                    "PRIMARY KEY(id, repoUrl))")

            it.execSQL("DROP TABLE onlineModules")
            it.execSQL("ALTER TABLE onlineModules_new RENAME TO onlineModules")
        }

        private val MIGRATION_5_6 = Migration(5, 6) {
            it.execSQL("CREATE TABLE IF NOT EXISTS localModules_new (" +
                    "id TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "versionCode INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "state TEXT NOT NULL, " +
                    "updateJson TEXT NOT NULL, " +
                    "PRIMARY KEY(id))")

            it.execSQL("DROP TABLE localModules")
            it.execSQL("ALTER TABLE localModules_new RENAME TO localModules")
        }

        private val MIGRATION_6_7 = Migration(6, 7) {
            it.execSQL("CREATE TABLE IF NOT EXISTS localModules_new (" +
                    "id TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "versionCode INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "state TEXT NOT NULL, " +
                    "updateJson TEXT NOT NULL, " +
                    "ignoreUpdates INTEGER NOT NULL, " +
                    "PRIMARY KEY(id))")

            it.execSQL("DROP TABLE localModules")
            it.execSQL("ALTER TABLE localModules_new RENAME TO localModules")
        }

        private val MIGRATION_7_8 = Migration(7, 8) {
            it.execSQL("CREATE TABLE IF NOT EXISTS localModules_new (" +
                    "id TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "versionCode INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "state TEXT NOT NULL, " +
                    "updateJson TEXT NOT NULL, " +
                    "PRIMARY KEY(id))")

            it.execSQL("CREATE TABLE IF NOT EXISTS localModules_updatable (" +
                    "id TEXT NOT NULL, " +
                    "updatable INTEGER NOT NULL, " +
                    "PRIMARY KEY(id))")

            it.execSQL("DROP TABLE localModules")
            it.execSQL("ALTER TABLE localModules_new RENAME TO localModules")
        }
    }
}