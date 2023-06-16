package com.sanmer.mrepo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.entity.OnlineModuleEntity
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.database.entity.RepoMetadata

@Database(entities = [Repo::class, OnlineModuleEntity::class], version = 3)
abstract class RepoDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao

    companion object {
        val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL("CREATE TABLE IF NOT EXISTS repo_new (" +
                    "url TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "size INTEGER NOT NULL, " +
                    "timestamp REAL NOT NULL, " +
                    "enable INTEGER NOT NULL, " +
                    "PRIMARY KEY(url))")

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

            it.execSQL("INSERT INTO repo_new (" +
                    "url, name, size, timestamp, enable) " +
                    "SELECT " +
                    "url, name, size, timestamp, enable " +
                    "FROM repo")
            it.execSQL("DROP TABLE repo")
            it.execSQL("ALTER TABLE repo_new RENAME TO repo")
        }

        val MIGRATION_2_3 = Migration(2, 3) {
            val default = RepoMetadata.default

            it.execSQL("CREATE TABLE IF NOT EXISTS repo_new (" +
                    "url TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "enable INTEGER NOT NULL, " +
                    "size INTEGER NOT NULL, " +
                    "timestamp REAL NOT NULL, " +
                    "version TEXT DEFAULT '${default.version}' NOT NULL, " +
                    "version_code INTEGER DEFAULT '${default.versionCode}' NOT NULL, " +
                    "PRIMARY KEY(url))")

            it.execSQL("INSERT INTO repo_new (" +
                    "url, name, enable, size, timestamp) " +
                    "SELECT " +
                    "url, name, enable, size, timestamp " +
                    "FROM repo")
            it.execSQL("DROP TABLE repo")
            it.execSQL("ALTER TABLE repo_new RENAME TO repo")
        }
    }
}