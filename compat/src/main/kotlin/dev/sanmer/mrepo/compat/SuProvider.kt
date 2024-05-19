package dev.sanmer.mrepo.compat

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import dev.sanmer.mrepo.compat.stub.IServiceManager
import dev.sanmer.mrepo.compat.su.SuService
import dev.sanmer.mrepo.compat.su.SuShellInitializer
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object SuProvider {
    init {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setTimeout(10)
        )
    }

    private suspend fun checkPermission() = suspendCancellableCoroutine { continuation ->
        Shell.EXECUTOR.submit {
            runCatching {
                Shell.getShell()
            }.onSuccess {
                continuation.resume(Unit)
            }.onFailure {
                continuation.resumeWithException(it)
            }
        }
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

            RootService.bind(SuService.intent, connection)
            continuation.invokeOnCancellation {
                RootService.stop(SuService.intent)
            }
        }
    }

    @Throws(IllegalStateException::class)
    suspend fun launch(): IServiceManager {
        checkPermission()

        return try {
            getIServiceManager()
        } catch (e: TimeoutCancellationException) {
            throw IllegalStateException(e)
        }
    }
}