package com.sanmer.mrepo.provider.local

import android.content.Context
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.provider.api.Ksu
import com.sanmer.mrepo.provider.api.Magisk
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object LocalProvider {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @Synchronized
    fun getLocalAll(
        context: Context
    ) = runCatching {
        if (Status.Provider.isFailed) {
            SuProvider.init(context)
        }

        coroutineScope.launch(Dispatchers.IO) {
            getLocalAll().onFailure {
                Timber.e("getLocal: ${it.message}")
            }
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
            throw RuntimeException("SuProvider is not ready!")
        }

        if (!Status.Env.isSucceeded) {
            if (!Status.Env.isLoading) {
                EnvProvider.init()
            }

            Status.Local.setFailed()
            throw RuntimeException("EnvProvider is not ready!")
        }

        when {
            EnvProvider.isMagisk -> Magisk.getModulesList()
            EnvProvider.isKsu -> Ksu.getModulesList()
            else -> throw RuntimeException("unknown root provider: ${EnvProvider.context}")
        }.onSuccess {
            ModuleManager.insertLocal(it)
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
}