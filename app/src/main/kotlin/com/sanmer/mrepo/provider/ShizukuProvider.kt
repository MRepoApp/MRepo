package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.provider.shizuku.ShizukuService
import com.sanmer.mrepo.provider.stub.IFileManager
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.provider.stub.IProvider
import com.sanmer.mrepo.provider.stub.IServiceManager
import rikka.shizuku.Shizuku
import timber.log.Timber

object ShizukuProvider : IProvider, Shizuku.OnRequestPermissionResultListener {
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
            isAlive = true
            Timber.i("ShizukuProvider: IServiceManager created")
            Timber.d("ShizukuProvider: uid = $uid, pid = $pid, context = $seLinuxContext")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceOrNull = null
            isAlive = false
            Timber.w("ShizukuProvider: IServiceManager destroyed")
        }

    }

    override fun init() {
        if (!isBinderAlive) return

        if (isGranted) {
            if (Shizuku.getUid() == 2000) {
                Timber.e("ShizukuProvider: unsupported platform, adb (uid == 2000)")
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