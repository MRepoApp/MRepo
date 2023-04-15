package com.sanmer.mrepo.provider.di

import android.content.Context
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.isNotReady
import com.sanmer.mrepo.di.MainScope
import com.sanmer.mrepo.provider.SuProvider
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Provides
    @Singleton
    fun providesSuProvider(
        @ApplicationContext context: Context,
        @MainScope externalScope: CoroutineScope
    ): SuProvider {
        val suProvider = SuProvider(context)

        suProvider.state.onEach {
            if (it.isNotReady && Config.isRoot) {
                suProvider.init()
            }
        }.launchIn(externalScope)

        return suProvider
    }

    @Provides
    @Singleton
    fun providesModulesLocalApi(
        suProvider: SuProvider
    ) = suProvider.api

    @Provides
    @Singleton
    fun providesFileSystemManager(
        suProvider: SuProvider
    ) = FileSystemManager.getRemote(suProvider.fileSystemService)
}