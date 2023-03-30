package com.sanmer.mrepo.provider.api

import android.content.Context
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.utils.expansion.output
import com.topjohnwu.superuser.Shell
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File

object KsuApi : BaseApi() {
    init {
        System.loadLibrary("ksu")
    }

    private const val ksud = "/data/adb/ksud"
    val versionCode: Int
        external get

    override fun init(onSucceeded: () -> Unit, onFailed: () -> Unit) {
        Timber.i("initKsu")

        Shell.cmd("$ksud --version").submit {
            if (it.isSuccess) {
                version = it.output.uppercase()

                runCatching {
                    SuProvider.Root.ksuVersionCode
                }.onSuccess { versionCode ->
                    version += " ($versionCode)"
                }

                onSucceeded()
            } else {
                Timber.e("initKsu: ${it.output}")
                onFailed()
            }
        }
    }

    override fun enable(module: LocalModule) {
        Shell.cmd("$ksud module enable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.ENABLE
            } else {
                Timber.e("enable failed: ${it.output}")
            }
        }
    }

    override fun disable(module: LocalModule) {
        Shell.cmd("$ksud module disable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.DISABLE
            } else {
                Timber.e("disable failed: ${it.output}")
            }
        }
    }

    override fun remove(module: LocalModule) {
        Shell.cmd("$ksud module uninstall ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.REMOVE
            } else {
                Timber.e("uninstall failed: ${it.output}")
            }
        }
    }

    override fun install(
        context: Context,
        onConsole: (console: String) -> Unit,
        onSucceeded: (LocalModule) -> Unit,
        onFailed: () -> Unit,
        zipFile: File
    ) = install(
        context = context,
        onConsole = onConsole,
        onSucceeded = onSucceeded,
        onFailed = onFailed,
        zipFile = zipFile,
        cmd = "$ksud module install ${zipFile.absolutePath}"
    )

    private fun getLocal(obj: JSONObject) = LocalModule(
        id = obj.getString("id"),
        name = obj.optString("name", "unknown"),
        author = obj.optString("author", "unknown"),
        version = obj.optString("version", "unknown"),
        versionCode = obj.optInt("versionCode", -1),
        description = obj.optString("description", "unknown")
    )

    private fun getState(obj: JSONObject): State {
        val enabled = obj.getBoolean("enabled")
        val update = obj.getBoolean("update")
        val remove = obj.getBoolean("remove")

        if (remove) return State.REMOVE
        if (update) return State.UPDATE

        return if (enabled) {
            State.ENABLE
        } else {
            State.DISABLE
        }
    }

    override fun getModulesList() = runCatching {
        Timber.i("getLocal: $version")

        val out = Shell.cmd("$ksud module list").exec().out
        val text = out.joinToString("\n").ifBlank { "[]" }

        val array = JSONArray(text)
        val modules = (0 until array.length())
            .asSequence()
            .map { array.getJSONObject(it) }
            .map { obj ->
                val module = getLocal(obj)
                module.state = getState(obj)

                return@map module
            }.toList()

        return@runCatching modules
    }
}