package com.sanmer.mrepo.provider.api

import android.content.Context
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.local.LocalProvider
import com.sanmer.mrepo.utils.unzip
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import java.io.File

abstract class Api {
    var version: String = "unknown"
        protected set

    abstract fun init()
    abstract fun enable(module: LocalModule)
    abstract fun disable(module: LocalModule)
    abstract fun remove(module: LocalModule)

    protected fun install(
        context: Context,
        onConsole: (console: String) -> Unit = {},
        onSucceeded: (LocalModule) -> Unit = {},
        onFailed: () -> Unit = {},
        onFinished: () -> Unit = {},
        zipFile: File,
        cmd: String
    ) = Shell.cmd(cmd)
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

                LocalProvider.getLocal(
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

    abstract fun install(
        context: Context,
        onConsole: (console: String) -> Unit,
        onSucceeded: (LocalModule) -> Unit,
        onFailed: () -> Unit,
        onFinished: () -> Unit,
        zipFile: File
    )

    abstract fun getModulesList(): Result<List<LocalModule>>
}