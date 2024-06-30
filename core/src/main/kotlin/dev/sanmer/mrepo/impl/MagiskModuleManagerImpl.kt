package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.Platform
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleOpsCallback
import dev.sanmer.su.wrap.ThrowableWrapper.Companion.wrap
import java.io.File
import java.io.FileNotFoundException

internal class MagiskModuleManagerImpl : BaseModuleManagerImpl() {
    override fun getPlatform(): String {
        return Platform.Magisk.name
    }

    override fun enable(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            tags = listOf(
                Tag("remove", FileOp.Delete),
                Tag("disable", FileOp.Delete)
            ),
            id = id,
            callback = callback
        )
    }

    override fun disable(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            tags = listOf(
                Tag("remove", FileOp.Delete),
                Tag("disable", FileOp.Create)
            ),
            id = id,
            callback = callback
        )
    }

    override fun remove(id: String, callback: IModuleOpsCallback?) {
        moduleOps(
            tags = listOf(
                Tag("disable", FileOp.Delete),
                Tag("remove", FileOp.Create)
            ),
            id = id,
            callback = callback
        )
    }

    override fun install(path: String, callback: IInstallCallback?) {
        install(
            cmd = "magisk --install-module '${path}'",
            path = path,
            callback = callback
        )
    }

    private fun moduleOps(tags: List<Tag>, id: String, callback: IModuleOpsCallback?) {
        val moduleDir = File(modulesDir, id)
        if (!moduleDir.exists()) {
            callback?.onFailure(id, FileNotFoundException(moduleDir.path).wrap())
            return
        }

        runCatching {
            tags.forEach {
                val tag = File(moduleDir, it.name)
                when (it.op) {
                    FileOp.Delete -> if (tag.exists()) tag.delete()
                    FileOp.Create -> tag.createNewFile()
                }
            }
        }.onSuccess {
            callback?.onSuccess(id)
        }.onFailure {
            callback?.onFailure(id, it.wrap())
        }
    }

    private class Tag(
        val name: String,
        val op: FileOp
    )

    private enum class FileOp {
        Delete,
        Create
    }
}