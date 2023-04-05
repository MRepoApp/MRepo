package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Process
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Event
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

object SuProvider {
    val state = MutableStateFlow(Event.NON)
    val event: Event get() = state.value

    private lateinit var provider: ISuProvider
    val Root get() = provider

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

    fun init(context: Context) {
        if (EnvProvider.isRoot) {
            val intent = Intent(context, SuService::class.java)
            RootService.bind(intent, ISuConnection)
        }
    }

    private object ISuConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Timber.i("SuProvider init")
            provider = ISuProvider.Stub.asInterface(binder)
            state.value = Event.SUCCEEDED
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.w("SuProvider disable")
            state.value = Event.FAILED
        }
    }

    private class SuService : RootService() {
        private object Provider : ISuProvider.Stub() {
            override fun getPid(): Int = Process.myPid()
            override fun getContext(): String = SELinux.context
            override fun getEnforce(): Int = SELinux.enforce
            override fun getFileSystemService(): IBinder = FileSystemManager.getService()
        }

        override fun onBind(intent: Intent): IBinder = Provider
    }
}