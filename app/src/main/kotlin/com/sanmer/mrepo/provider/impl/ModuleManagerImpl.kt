package com.sanmer.mrepo.provider.impl

import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.provider.stub.IInstallCallback
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.provider.stub.IModuleOpsCallback
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

    private var _version: String = "unknown"
    private var _versionCode: Int = -1

    override fun getVersion(): String {
        if (_version == "unknown") {
            _version = runCatching { "su -v".exec() }
                .getOrDefault("unknown")
        }

        return _version
    }

    override fun getVersionCode(): Int {
        if (_versionCode == -1) {
            _versionCode = runCatching { "su -V".exec().toInt() }
                .getOrDefault(-1)
        }

        return _versionCode
    }

    override fun getModules(): List<LocalModule> {
        return modulesDir.listFiles().orEmpty()
            .mapNotNull { moduleDir ->
                runCatching {
                    readProps(moduleDir)
                        .toModule(
                            state = readState(moduleDir),
                            lastUpdated = readLastUpdated(moduleDir)
                        )
                }.getOrNull()
            }
    }

    override fun getModuleById(id: String): LocalModule? {
        return runCatching {
            val moduleDir = modulesDir.resolve(id)
            readProps(moduleDir)
                .toModule(
                    state = readState(moduleDir),
                    lastUpdated = readLastUpdated(moduleDir)
                )
        }.getOrNull()
    }

    override fun getModuleInfo(zipPath: String): LocalModule? {
        return runCatching {
            val zipFile = File(zipPath)
            val propFile = tmpDir.resolve(PROP_FILE)
            zipFile.unzip(tmpDir, PROP_FILE, true)

            val module = readProps(tmpDir).toModule()
            propFile.delete()

            module
        }.getOrNull()
    }

    override fun enable(id: String, callback: IModuleOpsCallback) {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) callback.onFailure(id, null)

        when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module enable $id".submit {
                    if (it.isSuccess) {
                        callback.onSuccess(id)
                    } else {
                        callback.onFailure(id, it.out.joinToString())
                    }
                }
            }
            Platform.MAGISK -> {
                runCatching {
                    dir.resolve("remove").apply { if (exists()) delete() }
                    dir.resolve("disable").apply { if (exists()) delete() }
                }.onSuccess {
                    callback.onSuccess(id)
                }.onFailure {
                    callback.onFailure(id, it.message)
                }
            }
        }
    }

    override fun disable(id: String, callback: IModuleOpsCallback) {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return callback.onFailure(id, null)

        when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module disable $id".submit {
                    if (it.isSuccess) {
                        callback.onSuccess(id)
                    } else {
                        callback.onFailure(id, it.out.joinToString())
                    }
                }
            }
            Platform.MAGISK -> {
                runCatching {
                    dir.resolve("remove").apply { if (exists()) delete() }
                    dir.resolve("disable").createNewFile()
                }.onSuccess {
                    callback.onSuccess(id)
                }.onFailure {
                    callback.onFailure(id, it.message)
                }
            }
        }
    }

    override fun remove(id: String, callback: IModuleOpsCallback) {
        val dir = modulesDir.resolve(id)
        if (!dir.exists()) return callback.onFailure(id, null)

        when (platform) {
            Platform.KERNELSU -> {
                "${platform.manager} module uninstall $id".submit {
                    if (it.isSuccess) {
                        callback.onSuccess(id)
                    } else {
                        callback.onFailure(id, it.out.joinToString())
                    }
                }
            }
            Platform.MAGISK -> {
                runCatching {
                    dir.resolve("disable").apply { if (exists()) delete() }
                    dir.resolve("remove").createNewFile()
                }.onSuccess {
                    callback.onSuccess(id)
                }.onFailure {
                    callback.onFailure(id, it.message)
                }
            }
        }
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

        val stdout = object : CallbackList<String?>() {
            override fun onAddElement(msg: String?) {
                msg?.let(callback::onStdout)
            }
        }

        val stderr = object : CallbackList<String?>() {
            override fun onAddElement(msg: String?) {
                msg?.let(callback::onStderr)
            }
        }

        val result = shell.newJob().add(cmd).to(stdout, stderr).exec()
        if (result.isSuccess) {
            val module = getModuleInfo(path)
            callback.onSuccess(module?.id ?: "unknown")
        } else {
            callback.onFailure()
        }
    }

    private fun readProps(moduleDir: File): Map<String, String>? {
        return moduleDir.resolve(PROP_FILE)
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
    }

    private fun readState(moduleDir: File): State {
        moduleDir.resolve("remove").apply {
            if (exists()) return State.REMOVE
        }

        moduleDir.resolve("disable").apply {
            if (exists()) return State.DISABLE
        }

        moduleDir.resolve("update").apply {
            if (exists()) return State.UPDATE
        }

        return State.ENABLE
    }

    private fun readLastUpdated(moduleDir: File): Long {
        MODULE_FILES.forEach { filename ->
            val file = moduleDir.resolve(filename)
            if (file.exists()) {
                return file.lastModified()
            }
        }

        return 0L
    }

    private fun Map<String, String>?.toModule(
        state: State = State.ENABLE,
        lastUpdated: Long = 0L
    ): LocalModule? {
        if (this == null) return null
        return LocalModule(
            id = getOrDefault("id", "unknown"),
            name = getOrDefault("name", "unknown"),
            version = getOrDefault("version", ""),
            versionCode = getOrDefault("versionCode", "-1").toInt(),
            author = getOrDefault("author", ""),
            description = getOrDefault("description", ""),
            updateJson = getOrDefault("updateJson", ""),
            state = state,
            lastUpdated = lastUpdated
        )
    }

    private fun String.exec() = ShellUtils.fastCmd(shell, this)

    private fun String.submit(cb: Shell.ResultCallback) = shell
        .newJob().add(this).to(ArrayList(), null)
        .submit(cb)

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