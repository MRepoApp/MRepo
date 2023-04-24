package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Process
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.api.ApiInitializerListener
import com.sanmer.mrepo.api.local.KernelSuModulesApi
import com.sanmer.mrepo.api.local.MagiskModulesApi
import com.sanmer.mrepo.api.local.ModulesLocalApi
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.model.module.LocalModule
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.io.File

class SuProvider(private val appContext: Context) : ISuProvider.Stub(), ModulesLocalApi {

    val state = MutableStateFlow(Event.NON)
    val event: Event get() = state.value

    private lateinit var mProvider: ISuProvider
    private lateinit var mApi: ModulesLocalApi

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(15)
        )
    }

    private class SuShellInitializer : Shell.Initializer() {
        override fun onInit(context: Context, shell: Shell): Boolean = shell.isRoot
    }

    fun init() {
        val intent = Intent(appContext, SuService::class.java)
        RootService.bind(intent, iSuConnection)
    }

    private val iSuConnection = object : ServiceConnection {

        private val listener = object : ApiInitializerListener {
            override fun onSuccess() {
                state.value = Event.SUCCEEDED
            }

            override fun onFailure() {
                state.value = Event.FAILED
            }

        }

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Timber.i("SuProvider init")
            mProvider = asInterface(binder)

            when (context) {
                Const.KSU_CONTEXT -> {
                    mApi = KernelSuModulesApi(appContext)
                        .build(listener)
                }
                Const.MAGISK_CONTEXT -> {
                    mApi = MagiskModulesApi(appContext)
                        .setSuProvider(mProvider)
                        .build(listener)
                }
                else -> {
                    Timber.e("unknown root provider: $context")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.w("SuProvider disable")
            state.value = Event.FAILED
        }
    }

    private class SuService : RootService() {
        override fun onBind(intent: Intent): IBinder = object : ISuProvider.Stub() {
            override fun getPid(): Int = Process.myPid()
            override fun getContext(): String = SELinux.context
            override fun getEnforce(): Int = SELinux.enforce
            override fun getFileSystemService(): IBinder = FileSystemManager.getService()
        }
    }

    override fun getPid(): Int = mProvider.pid
    override fun getContext(): String = mProvider.context
    override fun getEnforce(): Int = mProvider.enforce
    override fun getFileSystemService(): IBinder = mProvider.fileSystemService

    override val version: String get() = mApi.version
    override suspend fun getModules() = mApi.getModules()
    override fun enable(module: LocalModule) = mApi.enable(module)
    override fun disable(module: LocalModule) = mApi.disable(module)
    override fun remove(module: LocalModule) = mApi.remove(module)
    override fun install(
        console: (String) -> Unit,
        onSuccess: (LocalModule) -> Unit,
        onFailure: () -> Unit,
        zipFile: File
    ) = mApi.install(console, onSuccess, onFailure, zipFile)
}