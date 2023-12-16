package com.sanmer.mrepo.provider.impl

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.utils.extensions.unzip
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import java.io.File

class ModuleManagerImpl(
    private val shell: Shell,
    private val platform: Platform
) : IModuleManager.Stub() {
    private val modulesDir = File("/data/adb/modules")
    private val tmpDir = File("/data/local/tmp")

    private var _version: String = ""
    private var _versionCode: Int = Int.MIN_VALUE

    override fun getVersion(): String {
        if (_version.isBlank()) {
            _version = runCatching { "su -v".exec() }
                .getOrDefault("unknown")
        }

        return _version
    }

    override fun getVersionCode(): Int {
        if (_versionCode == Int.MIN_VALUE) {
            _versionCode = runCatching { "su -V".exec().toInt() }
                .getOrDefault(-1)
        }

        return _versionCode
    }

    override fun enable(id: String): Boolean {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return false

        return when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module enable $id".execResult()
            }
            Platform.MAGISK -> {
                dir.resolve("remove").apply { if (exists()) delete() }
                dir.resolve("disable").apply { if (exists()) delete() }
                true
            }
        }
    }

    override fun disable(id: String): Boolean {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return false

        return when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module disable $id".execResult()
            }
            Platform.MAGISK -> {
                dir.resolve("remove").apply { if (exists()) delete() }
                dir.resolve("disable").createNewFile()
            }
        }
    }

    override fun remove(id: String): Boolean {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return false

        return when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module uninstall $id".execResult()
            }
            Platform.MAGISK -> {
                dir.resolve("disable").apply { if (exists()) delete() }
                dir.resolve("remove").createNewFile()
            }
        }
    }

    override fun getModules(): List<LocalModule> {
        return modulesDir.listFiles().orEmpty()
            .mapNotNull { moduleDir ->
                readPropsAndState(moduleDir)
            }
    }

    override fun getModuleById(id: String): LocalModule? {
        val moduleDir = modulesDir.resolve(id)
        return readPropsAndState(moduleDir)
    }

    override fun install(path: String, msg: MutableList<String>): LocalModule? {
        val cmd = when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module install '${path}'"
            }
            Platform.MAGISK -> {
                "${platform.manager} --install-module '${path}'"
            }
        }

        val result = shell.newJob().add(cmd).to(msg, msg).exec()
        return if (result.isSuccess) {
            val tmpModuleDir = tmpDir.resolve("tmp_module")
                .apply {
                    if (!exists()) mkdirs()
                    File(path).unzip(this, "module.prop", true)
                }

            val module = readPropsAndState(tmpModuleDir)
            tmpModuleDir.deleteRecursively()

            module?.copy(state = State.UPDATE)
        } else {
            null
        }
    }

    private fun readPropsAndState(moduleDir: File): LocalModule? {
        val props = moduleDir.resolve("module.prop")
            .apply {
                if (!exists()) return null
            }
            .readText().lines()
            .associate { line ->
                val items = line.split("=", limit = 2).map { it.trim() }
                if (items.size != 2) {
                    "" to ""
                } else {
                    items[0] to items[1]
                }
            }

        return LocalModule(
            id = props.getOrDefault("id", "unknown"),
            name = props.getOrDefault("name", "unknown"),
            version = props.getOrDefault("version", ""),
            versionCode = props.getOrDefault("versionCode", "-1").toInt(),
            author = props.getOrDefault("author", ""),
            description = props.getOrDefault("description", ""),
            updateJson = props.getOrDefault("updateJson", ""),
            state = readState(moduleDir)
        )
    }

    private fun readState(path: File): State {
        path.resolve("remove").apply {
            if (exists()) return State.REMOVE
        }

        path.resolve("disable").apply {
            if (exists()) return State.DISABLE
        }

        path.resolve("update").apply {
            if (exists()) return State.UPDATE
        }

        return State.ENABLE
    }

    private fun String.exec() = ShellUtils.fastCmd(shell, this)

    private fun String.execResult() = ShellUtils.fastCmdResult(shell, this)
}