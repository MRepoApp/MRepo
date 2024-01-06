package dev.sanmer.mrepo.compat.impl

import android.os.SELinux
import android.system.Os
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import dev.sanmer.mrepo.compat.stub.IFileManager
import dev.sanmer.mrepo.compat.stub.IModuleManager
import dev.sanmer.mrepo.compat.stub.IServiceManager
import kotlin.system.exitProcess

internal class ServiceManagerImpl : IServiceManager.Stub() {
    private val main by lazy {
        Shell.Builder.create()
            .build("sh")
    }

    private val platform by lazy {
        when {
            "nsenter --mount=/proc/1/ns/mnt which magisk".execResult() -> Platform.MAGISK
            "which ksud".execResult() -> Platform.KERNELSU
            else -> throw IllegalArgumentException("unsupported platform: $seLinuxContext")
        }
    }

    private val moduleManager by lazy {
        when (platform) {
            Platform.KERNELSU -> KernelSUModuleManagerImpl(main)
            Platform.MAGISK -> MagiskModuleManagerImpl(main)
        }
    }

    private val fileManager by lazy {
        FileManagerImpl()
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

    override fun getFileManager(): IFileManager {
        return fileManager
    }

    override fun currentPlatform(): String {
        return platform.name
    }

    override fun destroy() {
        exitProcess(0)
    }

    private fun String.execResult() = ShellUtils.fastCmdResult(main, this)
}