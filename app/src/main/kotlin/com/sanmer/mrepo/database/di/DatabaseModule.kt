package com.sanmer.mrepo.database.di

import android.content.Context
import androidx.room.Room
import com.sanmer.mrepo.database.ModuleDatabase
import com.sanmer.mrepo.database.RepoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesModuleDatabase(
        @ApplicationContext context: Context
    ): ModuleDatabase {
        // MIGRATION
        dbRename(context, "mrepo", "module")

        return Room.databaseBuilder(context,
            ModuleDatabase::class.java, "module")
            .build()
    }

    @Provides
    @Singleton
    fun providesModuleDao(db: ModuleDatabase) = db.moduleDao()

    @Provides
    @Singleton
    fun providesRepoDatabase(
        @ApplicationContext context: Context
    ): RepoDatabase {
        // MIGRATION
        context.filesDir.resolve("repositories").deleteRecursively()

        return Room.databaseBuilder(context,
            RepoDatabase::class.java, "repo")
            .addMigrations(RepoDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun providesRepoDao(db: RepoDatabase) = db.repoDao()

    @Suppress("SameParameterValue")
    private fun dbRename(context: Context, old: String, new: String) {
        context.databaseList().forEach {
            if (it.startsWith(old)) {
                val oldFile = context.getDatabasePath(it)
                val newFile = context.getDatabasePath(it.replace(old, new))
                oldFile.renameTo(newFile)
            }
        }
    }
}