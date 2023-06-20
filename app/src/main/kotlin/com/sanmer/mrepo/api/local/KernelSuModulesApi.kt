package com.sanmer.mrepo.api.local

import android.content.Context
import com.sanmer.mrepo.api.ApiInitializerListener
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.model.module.State
import com.sanmer.mrepo.utils.ModuleUtils
import com.sanmer.mrepo.utils.expansion.output
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File

class KernelSuModulesApi(private val context: Context) {
    private var version = "kernelsu"
    private val ksud = "/data/adb/ksud"

    fun build(listener: ApiInitializerListener): ModulesLocalApi {
        Timber.i("initKernelSu")

        Shell.cmd("su -v").submit {
            if (it.isSuccess) {
                val versionCode = ShellUtils.fastCmd("su -V")
                version = "${it.output} ($versionCode)"
                listener.onSuccess()

            } else {
                Timber.e("initKernelSu: ${it.output}")
                listener.onFailure()
            }
        }

        return object : ModulesLocalApi {
            val api = this@KernelSuModulesApi
            override val version: String get() = api.version
            override suspend fun getModules() = api.getModules()
            override fun enable(module: LocalModule) = api.enable(module)
            override fun disable(module: LocalModule) = api.disable(module)
            override fun remove(module: LocalModule) = api.remove(module)
            override fun install(
                console: (String) -> Unit,
                onSuccess: (LocalModule) -> Unit,
                onFailure: () -> Unit,
                zipFile: File
            ) = api.install(console, onSuccess, onFailure, zipFile)
        }
    }

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

    private suspend fun getModules() = runCatching {
        Timber.i("getLocal: $version")

        val out = Shell.cmd("$ksud module list").exec().out
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

    private fun enable(module: LocalModule) {
        Shell.cmd("$ksud module enable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.ENABLE
            } else {
                Timber.e("enable failed: ${it.output}")
            }
        }
    }

    private fun disable(module: LocalModule) {
        Shell.cmd("$ksud module disable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.DISABLE
            } else {
                Timber.e("disable failed: ${it.output}")
            }
        }
    }

    private fun remove(module: LocalModule) {
        Shell.cmd("$ksud module uninstall ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.REMOVE
            } else {
                Timber.e("uninstall failed: ${it.output}")
            }
        }
    }

    private fun install(
        console: (String) -> Unit,
        onSuccess: (LocalModule) -> Unit,
        onFailure: () -> Unit,
        zipFile: File
    ) = ModuleUtils.install(
        context = context,
        console = console,
        onSuccess = onSuccess,
        onFailure = onFailure,
        zipFile = zipFile,
        cmd = "$ksud module install ${zipFile.absolutePath}"
    )
}