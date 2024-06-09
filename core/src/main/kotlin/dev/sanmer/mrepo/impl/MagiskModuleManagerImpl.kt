package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.Platform
import dev.sanmer.mrepo.stub.IInstallCallback
import dev.sanmer.mrepo.stub.IModuleOpsCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

internal class MagiskModuleManagerImpl(
    private val managerScope: CoroutineScope
) : BaseModuleManagerImpl(managerScope) {
    override fun getPlatform() = Platform.Magisk.name

    override fun enable(id: String, callback: IModuleOpsCallback) {
        moduleOps(
            tags = listOf(
                Tag("remove", FileOp.Delete),
                Tag("disable", FileOp.Delete)
            ),
            id = id,
            callback = callback
        )
    }

    override fun disable(id: String, callback: IModuleOpsCallback) {
        moduleOps(
            tags = listOf(
                Tag("remove", FileOp.Delete),
                Tag("disable", FileOp.Create)
            ),
            id = id,
            callback = callback
        )
    }

    override fun remove(id: String, callback: IModuleOpsCallback) {
        moduleOps(
            tags = listOf(
                Tag("disable", FileOp.Delete),
                Tag("remove", FileOp.Create)
            ),
            id = id,
            callback = callback
        )
    }

    override fun install(path: String, callback: IInstallCallback) {
        install(
            cmd = "magisk --install-module '${path}'",
            path = path,
            callback = callback
        )
    }

    private fun moduleOps(
        tags: List<Tag>,
        id: String,
        callback: IModuleOpsCallback
    ) {
        managerScope.launch {
            val moduleDir = File(modulesDir, id)
            if (!moduleDir.exists()) {
                return@launch callback.onFailure(id, null)
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
                callback.onSuccess(id)
            }.onFailure {
                callback.onFailure(id, it.message)
            }
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