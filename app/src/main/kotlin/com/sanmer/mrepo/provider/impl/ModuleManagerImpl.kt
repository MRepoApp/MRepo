package com.sanmer.mrepo.provider.impl

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.provider.stub.IInstallCallback
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.utils.extensions.unzip
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import java.io.File

class ModuleManagerImpl(
    private val shell: Shell,
    private val platform: Platform
) : IModuleManager.Stub() {
    private val modulesDir = File(MODULES_PATH)
    private val tmpDir = File(TMP_PATH).apply {
        if (!exists()) mkdirs()
    }

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

    override fun install(path: String, callback: IInstallCallback) {
        val cmd = when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module install '${path}'"
            }
            Platform.MAGISK -> {
                "${platform.manager} --install-module '${path}'"
            }
        }

        val msg = object : CallbackList<String>() {
            override fun onAddElement(msg: String) {
                callback.console(msg)
            }
        }

        val result = shell.newJob().add(cmd).to(msg, msg).exec()
        if (result.isSuccess) {
            val file = tmpDir.resolve(PROP_FILE)
            File(path).unzip(tmpDir, PROP_FILE, true)

            val id = file.readText().lines()
                .firstOrNull { it.startsWith("id") }
                ?.split("=", limit = 2)
                ?.getOrNull(1)

            file.delete()
            callback.onSuccess(id ?: "unknown")
        } else {
            callback.onFailure()
        }
    }

    private fun readPropsAndState(moduleDir: File): LocalModule? {
        val props = moduleDir.resolve(PROP_FILE)
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
            state = readState(moduleDir),
            lastUpdated = readLastUpdated(moduleDir)
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

    private fun readLastUpdated(path: File): Long {
        MODULE_FILES.forEach { filename ->
            val file = path.resolve(filename)
            if (file.exists()) {
                return file.lastModified()
            }
        }

        return 0L
    }

    private fun String.exec() = ShellUtils.fastCmd(shell, this)

    private fun String.execResult() = ShellUtils.fastCmdResult(shell, this)

    companion object {
        const val PROP_FILE = "module.prop"
        const val MODULES_PATH = "/data/adb/modules"
        const val TMP_PATH = "/data/local/tmp"

        val MODULE_FILES = listOf(
            "post-fs-data.sh", "service.sh", "uninstall.sh",
            "system", "system.prop", "module.prop"
        )
    }
}