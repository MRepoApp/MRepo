package dev.sanmer.mrepo

import dev.sanmer.mrepo.content.Module
import dev.sanmer.mrepo.impl.APatchModuleManagerImpl
import dev.sanmer.mrepo.impl.KernelSUModuleManagerImpl
import dev.sanmer.mrepo.impl.MagiskModuleManagerImpl
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleManager
import dev.sanmer.mrepo.stub.IModuleOpsCallback

class ModuleManager : IModuleManager.Stub() {
    private val platform by lazy {
        when {
            "which magisk".exec().isSuccess -> Platform.Magisk
            "which ksud".exec().isSuccess -> Platform.KernelSU
            "which apd".exec().isSuccess -> Platform.APatch
            else -> throw IllegalArgumentException("Unsupported platform")
        }
    }

    private val original by lazy {
        when (platform) {
            Platform.Magisk -> MagiskModuleManagerImpl()
            Platform.KernelSU -> KernelSUModuleManagerImpl()
            Platform.APatch -> APatchModuleManagerImpl()
        }
    }

    override fun getVersion(): String = original.version
    override fun getVersionCode(): Int = original.versionCode
    override fun getPlatform(): String = original.platform
    override fun getModules(): List<Module> = original.modules
    override fun getModuleById(id: String): Module? = original.getModuleById(id)
    override fun getModuleInfo(path: String): Module? = original.getModuleInfo(path)
    override fun enable(id: String, callback: IModuleOpsCallback?) = original.enable(id, callback)
    override fun disable(id: String, callback: IModuleOpsCallback?) = original.disable(id, callback)
    override fun remove(id: String, callback: IModuleOpsCallback?) = original.remove(id, callback)
    override fun install(path: String, callback: IInstallCallback?) = original.install(path, callback)
    override fun deleteOnExit(path: String): Boolean = original.deleteOnExit(path)
    override fun reboot() = original.reboot()
}