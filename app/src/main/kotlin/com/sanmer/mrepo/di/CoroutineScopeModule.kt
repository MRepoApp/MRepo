package com.sanmer.mrepo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @ApplicationScope
    @Provides
    @Singleton
    fun providesDefaultCoroutineScope() = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    @MainScope
    @Provides
    @Singleton
    fun providesMainCoroutineScope() = CoroutineScope(
        SupervisorJob() + Dispatchers.Main
    )
}