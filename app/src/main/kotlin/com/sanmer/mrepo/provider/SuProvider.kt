package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.provider.stub.IFileManager
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.provider.stub.IProvider
import com.sanmer.mrepo.provider.stub.IServiceManager
import com.sanmer.mrepo.provider.su.SuService
import com.sanmer.mrepo.provider.su.SuShellInitializer
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import timber.log.Timber

object SuProvider : IProvider {
    private val timber = Timber.tag("SuProvider")

    private var mServiceOrNull: IServiceManager? = null
    private val mService get() = checkNotNull(mServiceOrNull) {
        "IServiceManager haven't been received"
    }

    override val uid: Int get() = mService.uid
    override val pid: Int get() = mService.pid
    override val seLinuxContext: String get() = mService.seLinuxContext
    override val moduleManager: IModuleManager get() = mService.moduleManager
    override val fileManager: IFileManager get() = mService.fileManager

    override var isAlive by mutableStateOf(false)
        private set

    init {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_MOUNT_MASTER)
                .setTimeout(15)
        )
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mServiceOrNull = IServiceManager.Stub.asInterface(service)
            isAlive = true
            timber.i("IServiceManager created")
            timber.d("uid = $uid, pid = $pid, context = $seLinuxContext")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceOrNull = null
            isAlive = false
            timber.w("IServiceManager destroyed")
        }

    }

    override fun init() {
        RootService.bind(SuService.intent, connection)
    }

    override fun destroy() {
        RootService.stop(SuService.intent)
    }

}