package dev.sanmer.mrepo

import android.os.IBinder
import dev.sanmer.mrepo.impl.APatchModuleManagerImpl
import dev.sanmer.mrepo.impl.KernelSUModuleManagerImpl
import dev.sanmer.mrepo.impl.MagiskModuleManagerImpl
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.stub.IModuleManager
import dev.sanmer.su.IService
import dev.sanmer.su.IServiceManager
import dev.sanmer.su.ServiceManagerCompat.delegate

class ModuleManager : IService {
    override val name = "module"

    override fun create(service: IServiceManager): IBinder = when {
        "which magisk".exec().isSuccess -> MagiskModuleManagerImpl()
        "which ksud".exec().isSuccess -> KernelSUModuleManagerImpl()
        "which apd".exec().isSuccess -> APatchModuleManagerImpl()
        else -> throw IllegalArgumentException("Unsupported platform (${service.seLinuxContext})")
    }

    companion object {
        fun delegate(service: IServiceManager): IModuleManager =
            IModuleManager.Stub.asInterface(
                service.delegate(ModuleManager::class.java)
            )
    }
}