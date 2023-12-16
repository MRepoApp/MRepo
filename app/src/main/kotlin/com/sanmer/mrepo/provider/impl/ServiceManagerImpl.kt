package com.sanmer.mrepo.provider.impl

import android.os.IBinder
import android.os.SELinux
import android.system.Os
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.provider.stub.IServiceManager
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.nio.FileSystemManager
import java.io.File

class ServiceManagerImpl : IServiceManager.Stub() {
    private val main by lazy {
        Shell.Builder.create()
            .build("sh")
    }

    private val moduleManager by lazy {
        ModuleManagerImpl(
            shell = main,
            platform = getPlatform()
        )
    }

    override fun getUid(): Int {
        return Os.getuid()
    }

    override fun getPid(): Int {
        return Os.getpid()
    }

    override fun getSELinuxContext(): String {
        return SELinux.getContext()
    }

    override fun getModuleManager(): IModuleManager {
        return moduleManager
    }

    override fun getFileSystemService(): IBinder {
        return FileSystemManager.getService()
    }

    private fun getPlatform(): Platform {
        return when {
            ShellUtils.fastCmdResult(main,"which ${Platform.MAGISK.manager}") -> Platform.MAGISK
            File(Platform.KERNELSU.manager).exists() -> Platform.KERNELSU
            else -> throw IllegalArgumentException("unsupported platform: $seLinuxContext")
        }
    }
}