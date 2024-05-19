package dev.sanmer.mrepo.compat

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import dev.sanmer.mrepo.compat.shizuku.ShizukuService
import dev.sanmer.mrepo.compat.stub.IServiceManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object ShizukuProvider {
    private val isGranted get() = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED

    private suspend fun checkPermission() = suspendCancellableCoroutine { continuation ->
        if (isGranted) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        val listener = object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(
                requestCode: Int,
                grantResult: Int
            ) {
                Shizuku.removeRequestPermissionResultListener(this)
                continuation.resume(isGranted)
            }
        }

        Shizuku.addRequestPermissionResultListener(listener)
        continuation.invokeOnCancellation {
            Shizuku.removeRequestPermissionResultListener(listener)
        }
        Shizuku.requestPermission(listener.hashCode())
    }

    private suspend fun getIServiceManager() = withTimeout(Const.TIMEOUT_MILLIS) {
        suspendCancellableCoroutine { continuation ->
            val connection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    val service = IServiceManager.Stub.asInterface(binder)
                    continuation.resume(service)
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    continuation.resumeWithException(
                        IllegalStateException("IServiceManager destroyed")
                    )
                }

                override fun onBindingDied(name: ComponentName?) {
                    continuation.resumeWithException(
                        IllegalStateException("IServiceManager destroyed")
                    )
                }
            }

            Shizuku.bindUserService(ShizukuService(), connection)
            continuation.invokeOnCancellation {
                Shizuku.unbindUserService(ShizukuService(), connection, true)
            }
        }
    }

    @Throws(IllegalStateException::class)
    suspend fun launch(): IServiceManager {
        if (!Shizuku.pingBinder()) {
            throw IllegalStateException("Shizuku not running")
        }

        if (!checkPermission()) {
            throw IllegalStateException("Shizuku not authorized")
        }

        return try {
            getIServiceManager()
        } catch (e: TimeoutCancellationException) {
            throw IllegalStateException(e)
        }
    }
}