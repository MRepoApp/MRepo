package com.sanmer.mrepo.utils

import android.content.Context
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.utils.extensions.tmpDir
import com.sanmer.mrepo.utils.extensions.unzip
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber
import java.io.File

object ModuleUtils {
    fun install(
        context: Context,
        fs: FileSystemManager,
        console: (console: String) -> Unit = {},
        onSuccess: (LocalModule) -> Unit = {},
        onFailure: () -> Unit = {},
        zipFile: File,
        cmd: String
    ) = Shell.cmd(cmd)
        .to(object : CallbackList<String?>() {
            override fun onAddElement(str: String?) {
                str?.let(console)
            }
        })
        .submit { r ->
            if (!r.isSuccess) {
                onFailure()
            } else {
                val tmp = context.tmpDir.apply {
                    if (!exists()) mkdirs()
                }

                runCatching {
                    fs.getFile(zipFile.absolutePath)
                        .newInputStream().use {
                            it.unzip(tmp, "module.prop", true)
                        }
                }.onFailure {
                    Timber.e(it)
                    return@submit
                }

                getModule(
                    prop = tmp.resolve("module.prop")
                ).onSuccess { value ->
                    value.state = State.UPDATE
                    onSuccess(value)
                }

                Shell.cmd("rm -rf ${tmp.absolutePath}").submit()
            }
        }

    fun getModule(prop: File) = runCatching {
        val props = Shell.cmd("dos2unix < ${prop.absolutePath}").exec().out

        LocalModule().apply {
            for (line in props) {
                val text = line.split("=".toRegex(), 2).map { it.trim() }
                if (text.size != 2) {
                    continue
                }

                val key = text[0]
                val value = text[1]
                if (key.isEmpty() || key[0] == '#') {
                    continue
                }

                when (key) {
                    "id" -> id = value
                    "name" -> name = value
                    "version" -> version = value
                    "versionCode" -> versionCode = value.toInt()
                    "author" -> author = value
                    "description" -> description = value
                }
            }
        }
    }.onFailure {
        Timber.e(it, "parseProps")
    }

    fun reboot(reason: String = "") {
        if (reason == "recovery") {
            // KEYCODE_POWER = 26, hide incorrect "Factory data reset" message
            Shell.cmd("/system/bin/input keyevent 26").submit()
        }
        Shell.cmd("/system/bin/svc power reboot $reason || /system/bin/reboot $reason").submit()
    }

    fun getVersionDisplay(
        version: String,
        versionCode: Int
    ): String {
        val included = "\\(.*?${versionCode}.*?\\)".toRegex()
            .containsMatchIn(version)

        return if (included) {
            version
        } else {
            "$version (${versionCode})"
        }
    }
}