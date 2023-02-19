package com.sanmer.mrepo.provider.local

import android.content.Context
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.FileProvider
import com.sanmer.mrepo.utils.unzip
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import java.io.File

object ModuleUtils {
    private val fs = FileProvider

    fun install(
        context: Context,
        onConsole: (console: String) -> Unit = {},
        onSucceeded: (LocalModule) -> Unit = {},
        onFailed: () -> Unit = {},
        onFinished: () -> Unit = {},
        zipFile: File
    ) = Shell.cmd("magisk --install-module ${zipFile.absolutePath}")
        .to(object : CallbackList<String?>() {
            override fun onAddElement(str: String?) {
                str?.let(onConsole)
            }
        })
        .submit {
            if (it.isSuccess) {
                val tmp = context.cacheDir.resolve("tmp")
                if (!tmp.exists()) tmp.mkdirs()
                zipFile.unzip(tmp, "module.prop", true)

                LocalLoader.getLocal(
                    prop = tmp.resolve("module.prop")
                ).onSuccess { value ->
                    value.state = State.UPDATE
                    onSucceeded(value)
                }.onFailure {
                    onFailed()
                }

                Shell.cmd("rm -rf ${tmp.absolutePath}").submit()
            } else {
                onFailed()
            }

            context.cacheDir.resolve("install.zip").delete()
            onFinished()
        }

    fun LocalModule.enable() {
        val path = "${Const.MODULES_PATH}/$id"
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
        val path = "${Const.MODULES_PATH}/$id"
        fs.getFile(path, "disable").createNewFile()
        state = State.DISABLE
    }

    fun LocalModule.remove() {
        val path = "${Const.MODULES_PATH}/$id"
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