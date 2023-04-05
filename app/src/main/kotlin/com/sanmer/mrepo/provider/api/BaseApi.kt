package com.sanmer.mrepo.provider.api

import android.content.Context
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.local.LocalProvider
import com.sanmer.mrepo.utils.expansion.output
import com.sanmer.mrepo.utils.expansion.unzip
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import java.io.File

abstract class BaseApi {
    var version: String = "unknown"
        protected set

    abstract fun init(onSucceeded: () -> Unit, onFailed: () -> Unit)
    abstract fun enable(module: LocalModule)
    abstract fun disable(module: LocalModule)
    abstract fun remove(module: LocalModule)

    protected fun getVersion(
        onSucceeded: () -> Unit = {},
        onFailed: (Shell.Result) -> Unit = {}
    ) = Shell.cmd("su -v").submit {
        if (it.isSuccess) {
            val versionCode = ShellUtils.fastCmd("su -V")
            version = "${it.output} ($versionCode)"
            onSucceeded()
        } else {
            onFailed(it)
        }
    }

    protected fun install(
        context: Context,
        onConsole: (console: String) -> Unit = {},
        onSucceeded: (LocalModule) -> Unit = {},
        onFailed: () -> Unit = {},
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

                LocalProvider.getModule(
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
        }

    abstract fun install(
        context: Context,
        onConsole: (console: String) -> Unit,
        onSucceeded: (LocalModule) -> Unit,
        onFailed: () -> Unit,
        zipFile: File
    )

    abstract suspend fun getModules(): Result<List<LocalModule>>
}