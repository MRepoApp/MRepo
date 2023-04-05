package com.sanmer.mrepo.provider.api

import android.content.Context
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.utils.expansion.output
import com.topjohnwu.superuser.Shell
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File

object KsuApi : BaseApi() {
    private const val KSUD = "/data/adb/ksud"

    override fun init(onSucceeded: () -> Unit, onFailed: () -> Unit) {
        Timber.i("initKsu")

        getVersion(onSucceeded) {
            Timber.e("initKsu: ${it.output}")
            onFailed()
        }
    }

    override fun enable(module: LocalModule) {
        Shell.cmd("$KSUD module enable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.ENABLE
            } else {
                Timber.e("enable failed: ${it.output}")
            }
        }
    }

    override fun disable(module: LocalModule) {
        Shell.cmd("$KSUD module disable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.DISABLE
            } else {
                Timber.e("disable failed: ${it.output}")
            }
        }
    }

    override fun remove(module: LocalModule) {
        Shell.cmd("$KSUD module uninstall ${module.id}").submit {
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
        cmd = "$KSUD module install ${zipFile.absolutePath}"
    )

    private fun getModule(obj: JSONObject) = LocalModule(
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

    override suspend fun getModules() = runCatching {
        Timber.i("getLocal: $version")

        val out = Shell.cmd("$KSUD module list").exec().out
        val text = out.joinToString("\n").ifBlank { "[]" }

        val array = JSONArray(text)
        val modules = (0 until array.length())
            .asSequence()
            .map { array.getJSONObject(it) }
            .map { obj ->
                getModule(obj).apply {
                    state = getState(obj)
                }
            }.toList()

        return@runCatching modules
    }
}