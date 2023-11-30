package com.sanmer.mrepo.content

import android.content.Context
import com.sanmer.mrepo.model.local.LocalModule
import com.topjohnwu.superuser.nio.FileSystemManager
import java.io.File

interface ILocalManager {
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

    interface InitListener {
        fun onSuccess()
        fun onFailure()
    }

    enum class Platform(val context: String) {
        MAGISK(context = "u:r:magisk:s0"),
        KERNELSU(context = "u:r:su:s0")
    }

    companion object {
        fun String.toPlatform() = when (this) {
            Platform.MAGISK.context -> Platform.MAGISK
            Platform.KERNELSU.context -> Platform.KERNELSU
            else -> throw IllegalArgumentException("unknown platform: $this")
        }

        fun build(
            context: Context,
            platform: Platform,
            listener: InitListener,
            fs: FileSystemManager
        ): ILocalManager = when (platform) {
            Platform.MAGISK -> {
                MagiskManager(context, fs).apply {
                    init(listener)
                }
            }
            Platform.KERNELSU -> {
                KernelSuManager(context, fs).apply {
                    init(listener)
                }
            }
        }
    }
}