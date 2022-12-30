package com.sanmer.mrepo.provider.libsu

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.app.status.Event
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber

object SuFileService {
    private var remoteFS = FileSystemManager.getLocal()
    fun getFileSystemManager() = remoteFS

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(LibSuShellInitializer::class.java)
                .setFlags(Shell.FLAG_MOUNT_MASTER or Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(12)
        )
    }

    private class LibSuShellInitializer : Shell.Initializer() {
        override fun onInit(context: Context, shell: Shell): Boolean = shell.isRoot
    }

    fun init(context: Context) {
        val intent = Intent(context, LibSuFileService::class.java)
        intent.addCategory(RootService.CATEGORY_DAEMON_MODE)
        RootService.bind(intent, Connection)
    }

    fun stop(context: Context) {
        val intent = Intent(context, LibSuFileService::class.java)
        intent.addCategory(RootService.CATEGORY_DAEMON_MODE)
        RootService.stop(intent)
    }

    private object Connection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.i("libsu service connected")
            Status.FileSystem.event = Event.SUCCEEDED
            remoteFS = FileSystemManager.getRemote(service)
        }
        override fun onServiceDisconnected(name: ComponentName) {
            Timber.w("libsu service disconnected")
            Status.FileSystem.event = Event.FAILED
        }
    }

    private class LibSuFileService : RootService() {
        override fun onBind(intent: Intent): IBinder = FileSystemManager.getService()
    }
}