package com.sanmer.mrepo.utils.module

import android.content.Context
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.FileServer
import com.sanmer.mrepo.utils.ShellHelper
import com.sanmer.mrepo.utils.unzip
import com.topjohnwu.superuser.Shell
import java.io.File

object ModuleUtils {
    private val fs = FileServer

    fun install(
        context: Context,
        onConsole: (console: String) -> Unit = {},
        onSucceeded: (LocalModule) -> Unit = {},
        onFailed: () -> Unit = {},
        onFinished: () -> Unit = {},
        zipFile: File
    ) {
        ShellHelper.submit(
            command = "magisk --install-module $zipFile",
            onCallback = {
                it?.let(onConsole)
            },
            onSucceeded = {
                val tmp = context.cacheDir.resolve("tmp")
                tmp.deleteRecursively()
                tmp.mkdirs()
                zipFile.unzip(tmp, "module.prop", true)
                val module = ModuleLoader.parseProps(
                    props = Shell.cmd("dos2unix < ${tmp.absolutePath}/module.prop").exec().out
                )
                module.state = State.UPDATE
                Constant.local.sortBy { it.name }
                Shell.cmd("rm -rf ${tmp.absolutePath}").submit()
                onSucceeded(module)
            },
            onFailed = {
                onFailed()
            },
            onFinished = {
                context.cacheDir.resolve("install.zip").delete()
                onFinished()
            }
        )
    }

    fun LocalModule.enable() {
        val path = "${Const.MAGISK_PATH}/$id"
        when (state) {
            State.REMOVE -> {
                fs.getFile(path, "remove").delete()
            }
            State.DISABLE -> {
                fs.getFile(path, "disable").delete()
            }
            else -> {}
        }

        state = State.ENABLE
    }

    fun LocalModule.disable() {
        state = State.DISABLE
        val path = "${Const.MAGISK_PATH}/$id"
        fs.getFile(path, "disable").createNewFile()
    }

    fun LocalModule.remove() {
        val path = "${Const.MAGISK_PATH}/$id"
        when (state) {
            State.ENABLE -> {
                fs.getFile(path, "remove").createNewFile()
            }
            State.DISABLE -> {
                fs.getFile(path, "disable").delete()
                fs.getFile(path, "remove").createNewFile()
            }
            else -> {}
        }
        state = State.REMOVE
    }
}