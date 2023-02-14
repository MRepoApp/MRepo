package com.sanmer.mrepo.provider.fs

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.provider.IFSService
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber

object LibSuService {
    private lateinit var libsu: IFSService
    val fileSystemManager get() = try {
        FileSystemManager.getRemote(libsu.fileSystemService)
    } catch (e: Exception) {
        FileSystemManager.getLocal()
    }

    private class SuShellInitializer : Shell.Initializer() {
        override fun onInit(context: Context, shell: Shell): Boolean = shell.isRoot
    }

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setFlags(Shell.FLAG_MOUNT_MASTER or Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(15)
        )
    }

    private fun getIntent(context: Context) = Intent(context, FSService::class.java).apply {
        addCategory(RootService.CATEGORY_DAEMON_MODE)
    }

    fun start(context: Context) {
        RootService.bind(getIntent(context), Connection)
    }

    private object Connection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Timber.i("Libsu service connected")
            libsu = IFSService.Stub.asInterface(binder)
            Status.Provider.setSucceeded()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.w("Libsu service disconnected")
            Status.Provider.setFailed()
        }
    }

    private class FSService : RootService() {
        private object IFSBinder : IFSService.Stub() {
            override fun getFileSystemService(): IBinder {
                return FileSystemManager.getService()
            }
        }

        override fun onBind(intent: Intent): IBinder = IFSBinder
    }
}