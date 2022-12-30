package com.sanmer.mrepo.utils.module

import android.content.Context
import com.sanmer.mrepo.api.RepoApi
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.app.status.Event
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.data.json.Modules
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.provider.FileServer
import com.sanmer.mrepo.provider.libsu.SuFileService
import com.topjohnwu.superuser.Shell
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

object ModuleLoader {
    private val fs = FileServer
    fun getLocal() {
        Timber.i("getLocal: ${Const.MAGISK_PATH}")
        Status.Local.event = Event.LOADING
        val list = mutableListOf<LocalModule>()

        if (!Status.FileSystem.isSucceeded) {
            throw RuntimeException("FileSystemManager is not ready!")
        }

        try {
            fs.getFile(Const.MAGISK_PATH)
                .listFiles()
                .orEmpty()
                .filter { !it.isFile && !it.isHidden }
                .forEach {
                    val path = it.absolutePath
                    val module = parseProps(
                        props = Shell.cmd("dos2unix < $path/module.prop").exec().out
                    )
                    module.state = parseState(it)

                    list.add(module)
                }
        } catch (e: Exception) {
            Timber.e("getLocal: ${e.message}")
            Status.Local.event = Event.FAILED
        }

        Constant.insertLocal(list)
        Status.Local.event = Event.SUCCEEDED
    }

    private fun parseState(path: File): State {
        val removeFile = fs.getFile(path, "remove")
        val disableFile = fs.getFile(path, "disable")
        val updateFile = fs.getFile(path, "update")
        val riruFolder = fs.getFile(path, "riru")
        val zygiskFolder = fs.getFile(path, "zygisk")
        val unloaded = fs.getFile(zygiskFolder, "unloaded")

        if (riruFolder.exists() || path.name == "riru-core" ) {
            if (Const.isZygiskEnabled) {
                return State.RIRU_DISABLE
            }
        }

        if (zygiskFolder.exists()) {
            if (unloaded.exists()) {
                return State.ZYGISK_UNLOADED
            }
            if (!Const.isZygiskEnabled) {
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

    fun parseProps(props: List<String>): LocalModule {
        return try {
            LocalModule().apply {
                for (line in props) {
                    val prop = line.split("=".toRegex(), 2).map { it.trim() }
                    if (prop.size != 2) {
                        continue
                    }

                    val key = prop[0]
                    val value = prop[1]
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
        } catch (e: Exception) {
            Timber.e("parseProps: ${e.message}")
            LocalModule()

        }
    }

    fun getRepo() {
        Status.Online.event = Event.LOADING

        RepoApi.getModules().enqueue(object : Callback<Modules> {
            override fun onResponse(call: Call<Modules>, response: Response<Modules>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.apply {
                        Status.Online.timestamp = timestamp
                        Constant.insertOnline(modules)
                    }
                    Status.Online.event = Event.SUCCEEDED
                } else {
                    val errorBody = response.errorBody()
                    val error = errorBody?.string()

                    Timber.e("getRepo: $error")
                    Status.Online.event = Event.FAILED
                }
            }
            override fun onFailure(call: Call<Modules>, t: Throwable) {
                Timber.e("getRepo: ${t.message}")
                Status.Online.event = Event.FAILED
            }

        })
    }

    fun getAll(context: Context) {
        getRepo()

        try {
            getLocal()
        } catch (e: Exception) {
            if (Status.FileSystem.isFailed) SuFileService.init(context)
            Timber.e("getLocal: ${e.message}")
        }
    }
}