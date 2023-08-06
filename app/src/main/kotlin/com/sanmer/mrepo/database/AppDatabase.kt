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
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.VersionItemEntity
import com.sanmer.mrepo.utils.extensions.renameDatabase

@Database(
    entities = [
        Repo::class,
        OnlineModuleEntity::class,
        VersionItemEntity::class,
        LocalModuleEntity::class
    ],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun onlineDao(): OnlineDao
    abstract fun versionDao(): VersionDao
    abstract fun localDao(): LocalDao
    abstract fun joinDao(): JoinDao

    companion object {
        fun build(context: Context): AppDatabase {
            // MIGRATION TODO: Remove in next version
            context.deleteDatabase("module")
            context.renameDatabase("repo", "mrepo")

            return Room.databaseBuilder(context,
                AppDatabase::class.java, "mrepo")
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5
                )
                .build()
        }

        private val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL("CREATE TABLE IF NOT EXISTS online_module (" +
                    "id TEXT NOT NULL, " +
                    "repo_url TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "version_code INTEGER NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "license TEXT NOT NULL, " +
                    "zipUrl TEXT NOT NULL, " +
                    "changelog TEXT NOT NULL, " +
                    "PRIMARY KEY(id, repo_url))")

            it.execSQL("CREATE TABLE IF NOT EXISTS repo_new (" +
                    "url TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "size INTEGER NOT NULL, " +
                    "timestamp REAL NOT NULL, " +
                    "enable INTEGER NOT NULL, " +
                    "PRIMARY KEY(url))")

            it.execSQL("INSERT INTO repo_new (" +
                    "url, name, size, timestamp, enable) " +
                    "SELECT " +
                    "url, name, size, timestamp, enable " +
                    "FROM repo")

            it.execSQL("DROP TABLE repo")
            it.execSQL("ALTER TABLE repo_new RENAME TO repo")
        }

        private val MIGRATION_2_3 = Migration(2, 3) {
            it.execSQL("CREATE TABLE IF NOT EXISTS repo_new (" +
                    "url TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "enable INTEGER NOT NULL, " +
                    "size INTEGER NOT NULL, " +
                    "timestamp REAL NOT NULL, " +
                    "version TEXT NOT NULL, " +
                    "version_code INTEGER NOT NULL, " +
                    "PRIMARY KEY(url))")

            it.execSQL("INSERT INTO repo_new (" +
                    "url, name, enable, size, timestamp, version, version_code) " +
                    "SELECT " +
                    "url, name, enable, size, timestamp, '1.0.0', 240 " +
                    "FROM repo")

            it.execSQL("DROP TABLE repo")
            it.execSQL("ALTER TABLE repo_new RENAME TO repo")
        }

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

            it.execSQL("INSERT INTO onlineModules_new (" +
                    "id, repoUrl, name, version, versionCode, author, description, " +
                    "type, added, license, homepage, source, support, donate) " +
                    "SELECT " +
                    "id, repoUrl, name, version, versionCode, author, description, " +
                    "'UNKNOWN', 0, license, '', '', '', '' " +
                    "FROM onlineModules")

            it.execSQL("DROP TABLE onlineModules")
            it.execSQL("ALTER TABLE onlineModules_new RENAME TO onlineModules")
        }
    }
}