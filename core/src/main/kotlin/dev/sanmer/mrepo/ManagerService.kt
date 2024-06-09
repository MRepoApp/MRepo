package dev.sanmer.mrepo

import dev.sanmer.mrepo.impl.APatchModuleManagerImpl
import dev.sanmer.mrepo.impl.FileManagerImpl
import dev.sanmer.mrepo.impl.KernelSUModuleManagerImpl
import dev.sanmer.mrepo.impl.MagiskModuleManagerImpl
import dev.sanmer.mrepo.impl.PowerManagerImpl
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.stub.IFileManager
import dev.sanmer.mrepo.stub.IManagerService
import dev.sanmer.mrepo.stub.IModuleManager
import dev.sanmer.mrepo.stub.IPowerManager
import dev.sanmer.su.IServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ManagerService : IManagerService.Stub() {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val platform by lazy {
        when {
            "which magisk".exec().isSuccess -> Platform.Magisk
            "which ksud".exec().isSuccess -> Platform.KernelSU
            "which apd".exec().isSuccess -> Platform.APatch
            else -> throw IllegalArgumentException("Unsupported platform")
        }
    }

    private val moduleManager by lazy {
        when (platform) {
            Platform.Magisk -> MagiskModuleManagerImpl(managerScope)
            Platform.KernelSU -> KernelSUModuleManagerImpl(managerScope)
            Platform.APatch -> APatchModuleManagerImpl(managerScope)
        }
    }

    private val fileManager by lazy {
        FileManagerImpl()
    }

    private val powerManager by lazy {
        PowerManagerImpl(managerScope)
    }

    override fun getModuleManager(): IModuleManager = moduleManager

    override fun getFileManager(): IFileManager = fileManager

    override fun getPowerManager(): IPowerManager = powerManager

    companion object {
        val IServiceManager.managerService: IManagerService
            get() = asInterface(
                getService(ManagerService::class.java.name)
            )
    }
}