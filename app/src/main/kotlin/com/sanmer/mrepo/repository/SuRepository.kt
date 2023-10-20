package com.sanmer.mrepo.repository

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.provider.SuProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuRepository @Inject constructor(
    private val suProvider: SuProvider
) {
    val state get() = suProvider.state
    val isInitialized get() = suProvider.isInitialized
    val fs get() = suProvider.getFileSystemManager()

    private val api get() = suProvider.getModulesApi()
    val version get() = api.version
    suspend fun getModules(): Result<List<LocalModule>> = api.getModules()
    fun enable(module: LocalModule) = api.enable(module)
    fun disable(module: LocalModule) = api.disable(module)
    fun remove(module: LocalModule) = api.remove(module)
    fun install(
        console: (String) -> Unit,
        onSuccess: (LocalModule) -> Unit,
        onFailure: () -> Unit,
        zipFile: File
    ) = api.install(console, onSuccess, onFailure, zipFile)
}