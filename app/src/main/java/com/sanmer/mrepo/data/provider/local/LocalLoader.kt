package com.sanmer.mrepo.data.provider.local

import android.content.Context
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.FileProvider
import com.sanmer.mrepo.provider.api.MagiskApi
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object LocalLoader {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val fs = FileProvider

    fun getLocalAll(
        context: Context,
        onFinished: (Boolean) -> Unit = {}
    ) = runCatching {
        if (Status.Provider.isFailed) {
            FileProvider.init(context)
        }

        coroutineScope.launch(Dispatchers.IO) {
            getLocalAll().onFailure {
                Timber.e("getLocal: ${it.message}")
            }
            onFinished(Status.Local.isSucceeded)
        }
    }.onFailure {
        Timber.e("getLocal: ${it.message}")
    }

    suspend fun getLocalAll() = withContext(Dispatchers.Default) {
        if (Status.Local.isLoading) {
            return@withContext Result.failure(RuntimeException("getLocal is already loading!"))
        } else {
            Status.Local.setLoading()
        }

        if (!Status.Provider.isSucceeded) {
            Status.Local.setFailed()
            throw RuntimeException("FileProvider is not ready!")
        }

        if (!Status.Env.isSucceeded) {
            if (!Status.Env.isLoading) {
                EnvProvider.init()
            }

            Status.Local.setFailed()
            throw RuntimeException("EnvProvider is not ready!")
        }

        Timber.i("getLocal: ${Const.MODULES_MOUNT_PATH}")
        runCatching {
            val result = mutableListOf<LocalModule>()
            fs.getFile(Const.MODULES_MOUNT_PATH).listFiles().orEmpty()
                .filter { !it.isFile && !it.isHidden }
                .forEach { path ->
                    getLocal(
                        prop = path.resolve("module.prop")
                    ).onSuccess { module ->
                        module.state = getState(path)
                        result.add(module)
                    }
                }

            return@runCatching result
        }.onSuccess {
            Constant.insertLocal(it)
            Status.Local.setSucceeded()
        }.onFailure {
            Timber.e("getLocal: ${it.message}")
            Status.Local.setFailed()
        }
    }

    fun getLocal(prop: File) = runCatching {
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
        Timber.e("parseProps: ${it.message}")
    }

    private fun getState(path: File): State {
        val removeFile = fs.getFile(path, "remove")
        val disableFile = fs.getFile(path, "disable")
        val updateFile = fs.getFile(path, "update")
        val riruFolder = fs.getFile(path, "riru")
        val zygiskFolder = fs.getFile(path, "zygisk")
        val unloaded = fs.getFile(zygiskFolder, "unloaded")

        if (riruFolder.exists() || path.name == "riru-core" ) {
            if (MagiskApi.isZygiskEnabled()) {
                return State.RIRU_DISABLE
            }
        }

        if (zygiskFolder.exists()) {
            if (unloaded.exists()) {
                return State.ZYGISK_UNLOADED
            }
            if (!MagiskApi.isZygiskEnabled()) {
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
}