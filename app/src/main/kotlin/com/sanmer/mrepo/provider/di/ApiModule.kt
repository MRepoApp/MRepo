package com.sanmer.mrepo.provider.di

import com.sanmer.mrepo.api.local.ModulesLocalApi
import com.sanmer.mrepo.provider.SuProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {

    @Binds
    @Singleton
    abstract fun bindsModulesLocalApi(suProvider: SuProvider): ModulesLocalApi

}