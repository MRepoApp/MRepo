package com.sanmer.mrepo.api.local

import android.content.Context
import com.sanmer.mrepo.api.ApiInitializerListener
import com.sanmer.mrepo.model.local.LocalModule
import com.topjohnwu.superuser.nio.FileSystemManager
import java.io.File

interface LocalApi {
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

    companion object {
        enum class Platform(val context: String) {
            MAGISK(context = "u:r:magisk:s0"),
            KERNELSU(context = "u:r:su:s0")
        }

        fun build(
            context: Context,
            platform: Platform,
            listener: ApiInitializerListener,
            fs: FileSystemManager
        ): LocalApi = when (platform) {
            Platform.MAGISK -> MagiskApi(context = context, fs = fs).build(listener)
            Platform.KERNELSU -> KernelSuApi(context = context, fs = fs).build(listener)
        }

        fun build(
            context: Context,
            attr: String,
            listener: ApiInitializerListener,
            fs: FileSystemManager
        ): LocalApi {
            val platform = when (attr) {
                Platform.MAGISK.context -> Platform.MAGISK
                Platform.KERNELSU.context -> Platform.KERNELSU
                else -> throw IllegalArgumentException("unknown platform: $context")
            }

            return build(
                context = context,
                platform = platform,
                listener = listener,
                fs = fs
            )
        }
    }
}