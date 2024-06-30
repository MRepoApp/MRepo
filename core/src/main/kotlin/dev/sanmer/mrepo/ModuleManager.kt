package dev.sanmer.mrepo

import android.os.IBinder
import dev.sanmer.mrepo.impl.APatchModuleManagerImpl
import dev.sanmer.mrepo.impl.KernelSUModuleManagerImpl
import dev.sanmer.mrepo.impl.MagiskModuleManagerImpl
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.su.IService
import dev.sanmer.su.IServiceManager

class ModuleManager : IService {
    override val name = "module"

    override fun create(manager: IServiceManager): IBinder = when {
        "which magisk".exec().isSuccess -> MagiskModuleManagerImpl()
        "which ksud".exec().isSuccess -> KernelSUModuleManagerImpl()
        "which apd".exec().isSuccess -> APatchModuleManagerImpl()
        else -> throw IllegalStateException("Unsupported platform (${manager.seLinuxContext})")
    }
}