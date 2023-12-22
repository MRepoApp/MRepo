package dev.sanmer.mrepo.compat

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Process
import android.util.Log
import dev.sanmer.mrepo.compat.shizuku.ShizukuService
import dev.sanmer.mrepo.compat.stub.IFileManager
import dev.sanmer.mrepo.compat.stub.IModuleManager
import dev.sanmer.mrepo.compat.stub.IProvider
import dev.sanmer.mrepo.compat.stub.IServiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import rikka.shizuku.Shizuku

object ShizukuProvider : IProvider, Shizuku.OnRequestPermissionResultListener {
    private const val TAG = "ShizukuProvider"
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

    private var isGranted = false
    private val isBinderAlive get() = Shizuku.pingBinder()

    init {
        if (isBinderAlive) {
            isGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
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
            Log.w(TAG, "IServiceManager destroyed")
        }

    }

    override fun init() {
        if (!isBinderAlive) return

        if (isGranted) {
            if (Shizuku.getUid() == Process.SHELL_UID) {
                Log.e(TAG, "unsupported platform: adb")
                return
            }

            Shizuku.bindUserService(ShizukuService(), connection)
        } else {
            Shizuku.addRequestPermissionResultListener(this)
            Shizuku.requestPermission(0)
        }
    }

    override fun destroy() {
        if (!isBinderAlive) return

        Shizuku.unbindUserService(ShizukuService(), connection, true)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        isGranted = grantResult == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            Shizuku.removeRequestPermissionResultListener(this)
            init()
        }
    }
}