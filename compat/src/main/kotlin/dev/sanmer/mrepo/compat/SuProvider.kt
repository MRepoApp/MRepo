package dev.sanmer.mrepo.compat

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import dev.sanmer.mrepo.compat.stub.IFileManager
import dev.sanmer.mrepo.compat.stub.IModuleManager
import dev.sanmer.mrepo.compat.stub.IProvider
import dev.sanmer.mrepo.compat.stub.IServiceManager
import dev.sanmer.mrepo.compat.su.SuService
import dev.sanmer.mrepo.compat.su.SuShellInitializer
import kotlinx.coroutines.flow.MutableStateFlow

object SuProvider : IProvider {
    private const val TAG = "SuProvider"
    private var mServiceOrNull: IServiceManager? = null
    private val mService get() = checkNotNull(mServiceOrNull) {
        "IServiceManager haven't been received"
    }

    override val uid: Int get() = mService.uid
    override val pid: Int get() = mService.pid
    override val seLinuxContext: String get() = mService.seLinuxContext
    override val moduleManager: IModuleManager get() = mService.moduleManager
    override val fileManager: IFileManager get() = mService.fileManager
    override val isKsu: Boolean get() = mService.isKsu

    override val isAlive = MutableStateFlow(false)

    init {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setTimeout(15)
        )
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mServiceOrNull = IServiceManager.Stub.asInterface(service)
            isAlive.value = true
            Log.i(TAG, "IServiceManager created")
            Log.d(TAG, "uid = $uid, pid = $pid, context = $seLinuxContext")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceOrNull = null
            isAlive.value = false
            Log.w(TAG, "SuProvider: IServiceManager destroyed")
        }

    }

    override fun init() {
        RootService.bind(SuService.intent, connection)
    }

    override fun destroy() {
        RootService.stop(SuService.intent)
    }

}