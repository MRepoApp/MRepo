package com.sanmer.mrepo.provider.di

import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.provider.SuProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {

    @Binds
    @Singleton
    abstract fun bindsSuProvider(suProviderImpl: SuProviderImpl): SuProvider

}