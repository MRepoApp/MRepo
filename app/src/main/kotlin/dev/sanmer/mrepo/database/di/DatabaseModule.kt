package dev.sanmer.mrepo.database.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.mrepo.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ) = AppDatabase.build(context)

    @Provides
    @Singleton
    fun providesRepoDao(db: AppDatabase) = db.repoDao()

    @Provides
    @Singleton
    fun providesLocalDao(db: AppDatabase) = db.localDao()
}