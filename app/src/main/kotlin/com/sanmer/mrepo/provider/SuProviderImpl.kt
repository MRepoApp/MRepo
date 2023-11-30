package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Process
import android.os.SELinux
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.content.ILocalManager
import com.sanmer.mrepo.content.ILocalManager.Companion.toPlatform
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SuProvider {
    override val state = MutableStateFlow(Event.NON)
    private val listener = object : ILocalManager.InitListener {
        override fun onSuccess() {
            state.value = Event.SUCCEEDED
            Timber.i("SuProviderImpl created")
        }

        override fun onFailure() {
            state.value = Event.FAILED
            Timber.w("SuProviderImpl destroyed")
        }
    }

    private lateinit var mProvider: ISuProvider
    private lateinit var mLocalManager: ILocalManager
    override val isInitialized get() =
        ::mProvider.isInitialized && ::mLocalManager.isInitialized

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_MOUNT_MASTER)
                .setTimeout(15)
        )
    }

    private class SuShellInitializer : Shell.Initializer() {
        override fun onInit(context: Context, shell: Shell): Boolean = shell.isRoot
    }

    fun init() {
        Timber.d("SuProviderImpl init")

        runCatching {
            if (!Shell.getShell().isRoot) {
                Timber.e("su request rejected (${Process.myUid()})")
                return@runCatching
            }

            Intent(context, SuService::class.java).apply {
                RootService.bind(this, connection)
            }
        }.onFailure {
            Timber.e(it, "SuProviderImpl init")
            listener.onFailure()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            mProvider = ISuProvider.Stub.asInterface(binder)

            runCatching {
                mLocalManager = ILocalManager.build(
                    context = context,
                    platform = mProvider.context.toPlatform(),
                    listener = listener,
                    fs = fs
                )
            }.onFailure {
                Timber.e(it)
                listener.onFailure()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            listener.onFailure()
        }
    }

    private class SuService : RootService() {
        override fun onBind(intent: Intent): IBinder = object : ISuProvider.Stub() {
            override fun getContext(): String = SELinux.getContext()
            override fun getFileSystemService(): IBinder = FileSystemManager.getService()
        }
    }

    override val fs get() = FileSystemManager.getRemote(mProvider.fileSystemService)
    override val lm get() = mLocalManager

}