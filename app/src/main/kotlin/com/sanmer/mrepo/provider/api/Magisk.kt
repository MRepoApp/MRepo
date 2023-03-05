package com.sanmer.mrepo.provider.api

import android.content.Context
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.FileProvider
import com.sanmer.mrepo.provider.local.LocalProvider
import com.sanmer.mrepo.utils.expansion.output
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import timber.log.Timber
import java.io.File

object Magisk : Api() {
    private val fs = FileProvider

    private lateinit var MAGISK_PATH: String
    private var isZygiskEnabled = false

    private val MODULES_MOUNT_PATH get() = "$MAGISK_PATH/modules"
    private const val MODULES_PATH = "/data/adb/modules"

    private fun isZygisk(): Boolean {
        val query = "SELECT value FROM settings WHERE key == \"zygisk\" LIMIT 1"
        val out = ShellUtils.fastCmd("magisk --sqlite '$query'")

        return if (out.isNotBlank()) {
            val map = out.split("\\|".toRegex())
                .map { it.split("=", limit = 2) }
                .filter { it.size == 2 }
                .associate { it[0] to it[1] }
            map["value"] == "1"
        } else {
            false
        }
    }

    override fun init() {
        Timber.i("initMagisk")
        Shell.cmd("magisk --path").submit {
            if (it.isSuccess) {
                MAGISK_PATH = "${it.output}/.magisk"
                version = ShellUtils.fastCmd("magisk -c")
                isZygiskEnabled = isZygisk()

                Timber.i("isZygiskEnabled: $isZygiskEnabled")
                Status.Env.setSucceeded()
            } else {
                Timber.e("initMagisk: ${it.output}")
                Status.Env.setFailed()
            }
        }
    }

    private val LocalModule.path get() = "${MODULES_PATH}/${id}"

    override fun enable(module: LocalModule) {
        when (module.state) {
            State.REMOVE -> {
                fs.getFile(module.path, "remove").delete()
            }
            State.DISABLE -> {
                fs.getFile(module.path, "disable").delete()
            }
            else -> {}
        }
        module.state = State.ENABLE
    }

    override fun disable(module: LocalModule) {
        fs.getFile(module.path, "disable").createNewFile()
        module.state = State.DISABLE
    }

    override fun remove(module: LocalModule) {
        when (module.state) {
            State.ENABLE -> {
                fs.getFile(module.path, "remove").createNewFile()
            }
            State.DISABLE -> {
                fs.getFile(module.path, "disable").delete()
                fs.getFile(module.path, "remove").createNewFile()
            }
            else -> {}
        }
        module.state = State.REMOVE
    }

    override fun install(
        context: Context,
        onConsole: (console: String) -> Unit,
        onSucceeded: (LocalModule) -> Unit,
        onFailed: () -> Unit,
        onFinished: () -> Unit,
        zipFile: File
    ) = install(
        context = context,
        onConsole = onConsole,
        onSucceeded = onSucceeded,
        onFailed = onFailed,
        onFinished = onFinished,
        zipFile = zipFile,
        cmd = "magisk --install-module ${zipFile.absolutePath}"
    )

    private fun getState(path: File): State {
        val removeFile = fs.getFile(path, "remove")
        val disableFile = fs.getFile(path, "disable")
        val updateFile = fs.getFile(path, "update")
        val riruFolder = fs.getFile(path, "riru")
        val zygiskFolder = fs.getFile(path, "zygisk")
        val unloaded = fs.getFile(zygiskFolder, "unloaded")

        if (riruFolder.exists() || path.name == "riru-core" ) {
            if (isZygiskEnabled) {
                return State.RIRU_DISABLE
            }
        }

        if (zygiskFolder.exists()) {
            if (unloaded.exists()) {
                return State.ZYGISK_UNLOADED
            }
            if (!isZygiskEnabled) {
                return State.ZYGISK_DISABLE
            }
        }

        if (removeFile.exists()) return State.REMOVE
        if (updateFile.exists()) return State.UPDATE

        return if (disableFile.exists()) {
            State.DISABLE
        } else {
            State.ENABLE
        }
    }

    override fun getModulesList() = runCatching {
        Timber.i("getLocal: $MODULES_MOUNT_PATH")

        val modules = mutableListOf<LocalModule>()
        fs.getFile(MODULES_MOUNT_PATH).listFiles().orEmpty()
            .filter { !it.isFile && !it.isHidden }
            .forEach { path ->
                LocalProvider.getLocal(
                    prop = path.resolve("module.prop")
                ).onSuccess { module ->
                    module.state = getState(path)
                    modules.add(module)
                }
            }

        return@runCatching modules.toList()
    }
}