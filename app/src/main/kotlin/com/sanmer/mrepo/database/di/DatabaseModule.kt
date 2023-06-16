package com.sanmer.mrepo.database.di

import android.content.Context
import androidx.room.Room
import com.sanmer.mrepo.database.ModuleDatabase
import com.sanmer.mrepo.database.RepoDatabase
import com.sanmer.mrepo.database.dao.ModuleDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.utils.expansion.renameDatabase
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
        context.renameDatabase("mrepo", "module")

        return Room.databaseBuilder(context,
            ModuleDatabase::class.java, "module")
            .build()
    }

    @Provides
    @Singleton
    fun providesModuleDao(db: ModuleDatabase): ModuleDao = db.moduleDao()

    @Provides
    @Singleton
    fun providesRepoDatabase(
        @ApplicationContext context: Context
    ): RepoDatabase {
        // MIGRATION
        context.filesDir.resolve("repositories").deleteRecursively()

        return Room.databaseBuilder(context,
            RepoDatabase::class.java, "repo")
            .addMigrations(
                RepoDatabase.MIGRATION_1_2,
                RepoDatabase.MIGRATION_2_3
            )
            .build()
    }

    @Provides
    @Singleton
    fun providesRepoDao(db: RepoDatabase): RepoDao = db.repoDao()
}