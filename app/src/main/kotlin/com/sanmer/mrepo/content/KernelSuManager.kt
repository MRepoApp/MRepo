package com.sanmer.mrepo.content

import android.content.Context
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.utils.ModuleUtils
import com.sanmer.mrepo.utils.extensions.output
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.nio.FileSystemManager
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File

class KernelSuManager(
    private val context: Context,
    private val fs: FileSystemManager
) : ILocalManager {
    private val ksud = "/data/adb/ksud"

    override var version = "kernelsu"

    fun init(listener: ILocalManager.InitListener) {
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

    override suspend fun getModules() = runCatching {
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

    override fun enable(module: LocalModule) {
        Shell.cmd("$ksud module enable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.ENABLE
            }
        }
    }

    override fun disable(module: LocalModule) {
        Shell.cmd("$ksud module disable ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.DISABLE
            }
        }
    }

    override fun remove(module: LocalModule) {
        Shell.cmd("$ksud module uninstall ${module.id}").submit {
            if (it.isSuccess) {
                module.state = State.REMOVE
            }
        }
    }

    override fun install(
        console: (String) -> Unit,
        onSuccess: (LocalModule) -> Unit,
        onFailure: () -> Unit,
        zipFile: File
    ) = ModuleUtils.install(
        context = context,
        fs = fs,
        console = console,
        onSuccess = onSuccess,
        onFailure = onFailure,
        zipFile = zipFile,
        cmd = "$ksud module install '${zipFile.absolutePath}'"
    )
}