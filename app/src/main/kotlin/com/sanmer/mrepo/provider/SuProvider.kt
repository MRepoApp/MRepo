package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Process
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.provider.api.Ksu
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber

object SuProvider {
    lateinit var Root: ISuProvider

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
        val intent = Intent(context, SuService::class.java).apply {
            addCategory(RootService.CATEGORY_DAEMON_MODE)
        }
        RootService.bind(intent, Connection)
    }

    fun close(context: Context) {
        val intent = Intent(context, SuService::class.java)
        RootService.bind(intent, Connection)
    }

    private object Connection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Timber.i("SuProvider init")
            Root = ISuProvider.Stub.asInterface(binder)
            Status.Provider.setSucceeded()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.w("SuProvider close")
            Status.Provider.setFailed()
        }
    }

    private class SuService : RootService() {
        private object Provider : ISuProvider.Stub() {
            override fun getPid(): Int = Process.myPid()
            override fun getContext(): String = SELinux.context
            override fun isSelinuxEnabled(): Boolean = SELinux.isSelinuxEnabled()
            override fun getEnforce(): Boolean = SELinux.getEnforce()
            override fun getContextByPid(pid: Int) = SELinux.getContextByPid(pid)
            override fun getFileSystemService(): IBinder = FileSystemManager.getService()

            override fun getKsuVersionCode(): Int = Ksu.versionCode
        }

        override fun onBind(intent: Intent): IBinder = Provider
    }
}