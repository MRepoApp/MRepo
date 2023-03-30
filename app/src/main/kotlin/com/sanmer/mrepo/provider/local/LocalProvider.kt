package com.sanmer.mrepo.provider.local

import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.provider.api.KsuApi
import com.sanmer.mrepo.provider.api.MagiskApi
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object LocalProvider {
    suspend fun getLocalAll() = withContext(Dispatchers.IO) {
        if (!SuProvider.event.isSucceeded) {
            throw RuntimeException("SuProvider is not ready!")
        }

        if (!EnvProvider.event.isSucceeded) {
            throw RuntimeException("EnvProvider is not ready!")
        }

        when {
            EnvProvider.isMagisk -> MagiskApi.getModulesList()
            EnvProvider.isKsu -> KsuApi.getModulesList()
            else -> throw RuntimeException("unknown root provider: ${EnvProvider.context}")
        }.onSuccess {
            ModuleManager.insertLocal(it)
        }.onFailure {
            Timber.e("getLocal: ${it.message}")
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