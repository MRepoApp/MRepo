package com.sanmer.mrepo.api.local

import com.sanmer.mrepo.model.local.LocalModule
import java.io.File

interface ModulesLocalApi {
    val version: String

    suspend fun getModules(): Result<List<LocalModule>>

    fun enable(module: LocalModule)

    fun disable(module: LocalModule)

    fun remove(module: LocalModule)

    fun install(
        console: (String) -> Unit,
        onSuccess: (LocalModule) -> Unit,
        onFailure: () -> Unit,
        zipFile: File
    )
}