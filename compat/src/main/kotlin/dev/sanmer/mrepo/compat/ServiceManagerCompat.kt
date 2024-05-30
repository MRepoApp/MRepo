package dev.sanmer.mrepo.compat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import dev.sanmer.mrepo.compat.delegate.ContextDelegate
import dev.sanmer.mrepo.compat.impl.ServiceManagerImpl
import dev.sanmer.mrepo.compat.stub.IServiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object ServiceManagerCompat {
    private const val TIMEOUT_MILLIS = 15_000L

    fun setHiddenApiExemptions() = when {
        BuildCompat.atLeastP -> HiddenApiBypass.addHiddenApiExemptions("")
        else -> true
    }

    internal interface IProvider {
        val name: String
        suspend fun isAvailable(): Boolean
        suspend fun isAuthorized(): Boolean
        fun bind(connection: ServiceConnection)
        fun unbind(connection: ServiceConnection)
    }

    private suspend fun get(provider: IProvider) = withTimeout(TIMEOUT_MILLIS) {
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

            provider.bind(connection)
            continuation.invokeOnCancellation {
                provider.unbind(connection)
            }
        }
    }

    private suspend fun from(provider: IProvider): IServiceManager = withContext(Dispatchers.Main) {
        when {
            !provider.isAvailable() -> throw IllegalStateException("${provider.name} not available")
            !provider.isAuthorized() -> throw IllegalStateException("${provider.name} not authorized")
            else -> get(provider)
        }
    }

    private class ShizukuProvider(
        private val context: Context
    ) : IProvider {
        override val name = "Shizuku"

        override suspend fun isAvailable(): Boolean {
            return Shizuku.pingBinder() && Shizuku.getUid() == 0
        }

        override suspend fun isAuthorized() = when {
            isGranted -> true
            else -> suspendCancellableCoroutine { continuation ->
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
        }

        override fun bind(connection: ServiceConnection) {
            Shizuku.bindUserService(service, connection)
        }

        override fun unbind(connection: ServiceConnection) {
            Shizuku.unbindUserService(service, connection, true)
        }

        private val isGranted get() = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED

        private val service by lazy {
            Shizuku.UserServiceArgs(
                ComponentName(
                    context.packageName,
                    ServiceManagerImpl::class.java.name
                )
            ).apply {
                daemon(false)
                debuggable(false)
                processNameSuffix("shizuku")
            }
        }

        companion object {
            private val provider by lazy {
                ShizukuProvider(
                    ContextDelegate.getContext()
                )
            }

            fun get(): IProvider = provider
        }
    }

    suspend fun fromShizuku() = from(ShizukuProvider.get())

    private class LibSuProvider(
        private val context: Context
    ) : IProvider {
        override val name = "LibSu"

        override suspend fun isAvailable() = true

        override suspend fun isAuthorized() = suspendCancellableCoroutine { continuation ->
            Shell.EXECUTOR.submit {
                runCatching {
                    Shell.getShell()
                }.onSuccess {
                    continuation.resume(true)
                }.onFailure {
                    continuation.resume(false)
                }
            }
        }

        override fun bind(connection: ServiceConnection) {
            RootService.bind(service, connection)
        }

        override fun unbind(connection: ServiceConnection) {
            RootService.stop(service)
        }

        private val service by lazy {
            Intent().apply {
                component = ComponentName(
                    context.packageName,
                    Service::class.java.name
                )
            }
        }

        init {
            Shell.enableVerboseLogging = true
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setInitializers(SuShellInitializer::class.java)
                    .setTimeout(10)
            )
        }

        private class SuShellInitializer : Shell.Initializer() {
            override fun onInit(context: Context, shell: Shell) = shell.isRoot
        }

        private class Service : RootService() {
            override fun onBind(intent: Intent): IBinder {
                return ServiceManagerImpl()
            }
        }

        companion object {
            private val provider by lazy {
                LibSuProvider(
                    ContextDelegate.getContext()
                )
            }

            fun get(): IProvider = provider
        }
    }

    suspend fun fromLibSu() = from(LibSuProvider.get())
}