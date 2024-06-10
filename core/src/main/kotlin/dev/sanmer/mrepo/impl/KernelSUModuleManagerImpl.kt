package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.Platform
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleOpsCallback
import java.io.File

internal class KernelSUModuleManagerImpl : BaseModuleManagerImpl() {
    override fun getPlatform(): String {
        return Platform.KernelSU.name
    }

    override fun enable(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            cmd = "ksud module enable $id",
            id = id,
            callback = callback
        )
    }

    override fun disable(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            cmd = "ksud module disable $id",
            id = id,
            callback = callback
        )
    }

    override fun remove(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            cmd = "ksud module uninstall $id",
            id = id,
            callback = callback
        )
    }

    override fun install(path: String, callback: IInstallCallback?) {
        install(
            cmd = "ksud module install '${path}'",
            path = path,
            callback = callback
        )
    }

    private fun moduleOps(cmd: String, id: String, callback: IModuleOpsCallback?) {
        val moduleDir = File(modulesDir, id)
        if (!moduleDir.exists()) {
            callback?.onFailure(id, null)
            return
        }

        cmd.exec().onSuccess {
            callback?.onSuccess(id)
        }.onFailure {
            callback?.onFailure(id, it.message)
        }
    }
}