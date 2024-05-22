package com.sanmer.mrepo.database.di

import android.content.Context
import com.sanmer.mrepo.database.AppDatabase
import com.sanmer.mrepo.database.dao.JoinDao
import com.sanmer.mrepo.database.dao.LocalDao
import com.sanmer.mrepo.database.dao.OnlineDao
import com.sanmer.mrepo.database.dao.RepoDao
import com.sanmer.mrepo.database.dao.VersionDao
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
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.build(context)

    @Provides
    @Singleton
    fun providesRepoDao(db: AppDatabase): RepoDao = db.repoDao()

    @Provides
    @Singleton
    fun providesOnlineDao(db: AppDatabase): OnlineDao = db.onlineDao()

    @Provides
    @Singleton
    fun providesVersionDao(db: AppDatabase): VersionDao = db.versionDao()

    @Provides
    @Singleton
    fun providesLocalDao(db: AppDatabase): LocalDao = db.localDao()

    @Provides
    @Singleton
    fun providesJoinDao(db: AppDatabase): JoinDao = db.joinDao()
}