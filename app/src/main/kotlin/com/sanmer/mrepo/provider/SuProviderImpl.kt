package com.sanmer.mrepo.provider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Process
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.api.ApiInitializerListener
import com.sanmer.mrepo.api.local.KernelSuModulesApi
import com.sanmer.mrepo.api.local.MagiskModulesApi
import com.sanmer.mrepo.api.local.ModulesLocalApi
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.app.isNotReady
import com.sanmer.mrepo.di.MainScope
import com.sanmer.mrepo.utils.expansion.toFile
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SuProviderImpl @Inject constructor(
    @MainScope mainScope: CoroutineScope,
    @ApplicationContext private val appContext: Context
) : SuProvider {
    override val state = MutableStateFlow(Event.NON)

    private lateinit var mProvider: ISuProvider
    private lateinit var mApi: ModulesLocalApi

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(SuShellInitializer::class.java)
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(15)
        )

        state.onEach { // TODO: Add observer to workingMode
            if (it.isNotReady && Config.isRoot) {
                init()
            }
        }.launchIn(mainScope)
    }

    private class SuShellInitializer : Shell.Initializer() {
        override fun onInit(context: Context, shell: Shell): Boolean = shell.isRoot
    }

    fun init() {
        val intent = Intent(appContext, SuService::class.java)
        RootService.bind(intent, connection)
    }

    private val connection = object : ServiceConnection {

        private val listener = object : ApiInitializerListener {
            override fun onSuccess() {
                state.value = Event.SUCCEEDED
            }

            override fun onFailure() {
                state.value = Event.FAILED
            }

        }

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Timber.i("SuProvider init")
            mProvider = ISuProvider.Stub.asInterface(binder)

            when (context) {
                Const.KSU_CONTEXT -> {
                    mApi = KernelSuModulesApi(
                        context = appContext
                    ).build(listener)
                }

                Const.MAGISK_CONTEXT -> {
                    mApi = MagiskModulesApi(
                        context = appContext,
                        fs = getFileSystemManager()
                    ).build(listener)
                }

                else -> {
                    Timber.e("unknown root provider: $context")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.w("SuProvider disable")
            state.value = Event.FAILED
        }
    }

    private class SuService : RootService() {
        override fun onBind(intent: Intent): IBinder = object : ISuProvider.Stub() {
            override fun getPid(): Int = Process.myPid()
            override fun getContext(): String = getContextImpl(pid)
            override fun getEnforce(): Int = getEnforceImpl()
            override fun getFileSystemService(): IBinder = FileSystemManager.getService()
        }

        private inline fun <T> safe(default: T, block: () -> T): T {
            return try {
                block()
            } catch (e: Throwable) {
                Timber.e(e)
                default
            }
        }

        private fun getContextImpl(pid: Int) = safe("unknown") {
            "/proc/$pid/attr/current".toFile()
                .readText()
                .replace("[^a-z0-9:_,]".toRegex(), "")
        }

        private fun getEnforceImpl() = safe(1) {
            "/sys/fs/selinux/enforce".toFile()
                .readText()
                .replace("[^0-9]".toRegex(), "")
                .toInt()
        }

    }

    override val pid: Int get() = mProvider.pid
    override val context: String get() = mProvider.context
    override val enforce: Int get() = mProvider.enforce

    override fun getFileSystemManager(): FileSystemManager =
        FileSystemManager.getRemote(mProvider.fileSystemService)

    override fun getModulesApi(): ModulesLocalApi = mApi

}