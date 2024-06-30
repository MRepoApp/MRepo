package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.Platform
import dev.sanmer.mrepo.impl.Shell.exec
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleOpsCallback
import dev.sanmer.su.wrap.ThrowableWrapper.Companion.wrap
import java.io.File
import java.io.FileNotFoundException

internal class APatchModuleManagerImpl : BaseModuleManagerImpl() {
    override fun getPlatform(): String {
        return Platform.APatch.name
    }

    override fun enable(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            cmd = "apd module enable $id",
            id = id,
            callback = callback
        )
    }

    override fun disable(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            cmd = "apd module disable $id",
            id = id,
            callback = callback
        )
    }

    override fun remove(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            cmd = "apd module uninstall $id",
            id = id,
            callback = callback
        )
    }

    override fun install(path: String, callback: IInstallCallback?) {
        install(
            cmd = "apd module install '${path}'",
            path = path,
            callback = callback
        )
    }

    private fun moduleOps(cmd: String, id: String, callback: IModuleOpsCallback?) {
        val moduleDir = File(modulesDir, id)
        if (!moduleDir.exists()) {
            callback?.onFailure(id, FileNotFoundException(moduleDir.path).wrap())
            return
        }

        cmd.exec().onSuccess {
            callback?.onSuccess(id)
        }.onFailure {
            callback?.onFailure(id, it.wrap())
        }
    }
}