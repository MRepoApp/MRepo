package com.sanmer.mrepo.service

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.ui.activity.install.InstallActivity
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.expansion.parcelable
import com.sanmer.mrepo.utils.expansion.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class DownloadService : LifecycleService() {
    private data class DownloadItem(
        val id: Int = System.currentTimeMillis().toInt(),
        val value: Module,
        val install: Boolean
    )
    private val list = mutableListOf<DownloadItem>()

    private fun stopIt() = list.isEmpty().let {
        if (it) lifecycleScope.launch {
            delay(5000)
            if (list.isEmpty()) stopSelf()
        }
    }

    private fun addToList(value: DownloadItem): Boolean =
        if (value.value in list.map { it.value }) {
            false
        } else {
            list.add(value)
        }

    override fun onCreate() {
        super.onCreate()
        Timber.d("DownloadService start")
        setForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val install = intent?.getBooleanExtra(INSTALL_KEY, false) == true
        val module = intent?.parcelable<Module>(MODULE_KEY)

        module?.let {
            Timber.i("download: ${module.name}")

            val new = addToList(DownloadItem(value = module, install = install))
            if (new) {
                downloader(list.last())
            } else {
                Timber.i("${module.name} is downloading!")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("DownloadService stop")
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun downloader(
        item: DownloadItem
    ) {
        val module = item.value
        val notificationId = item.id
        val notificationIdFinish = notificationId + 1

        val path = module.path.toFile() ?: Const.DOWNLOAD_PATH.resolve("${module.name}.zip")
        Timber.d("download to ${path.absolutePath}")

        val notification = NotificationUtils
            .buildNotification(this, Const.CHANNEL_ID_DOWNLOAD)
            .setContentTitle(module.name)
            .setContentIntent(NotificationUtils.getActivity(MainActivity::class))
            .setProgress(0, 0 , false)
            .setOngoing(true)
            .setGroup(GROUP_KEY)

        val progressFlow = MutableStateFlow(0)
        progressFlow.sample(500)
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it == 100) {
                    NotificationUtils.cancel(notificationId)
                    broadcast(0f, module)
                    return@onEach
                }

                NotificationUtils.notify(
                    notificationId,
                    notification.setProgress(100, it, false)
                        .build()
                )
            }.launchIn(lifecycleScope)

        val succeeded: () -> Unit = {
            val message = getString(R.string.message_download_success)
            notifyFinish(
                id = notificationIdFinish,
                name = module.name,
                message = message,
                silent = true
            )

            if (item.install) {
                InstallActivity.start(context = this, path = path)
            }
        }

        val failed: (String?) -> Unit = {
            broadcast(0f, module)

            val message = getString(R.string.message_download_failed, it)
            notifyFinish(
                id = notificationIdFinish,
                name = module.name,
                message = message
            )
        }

        HttpUtils.downloader(
            url = module.url,
            path = path,
            onProgress = {
                broadcast(it, module)
                progressFlow.value = (it * 100).toInt()
            },
            onSucceeded = succeeded,
            onFailed = failed,
            onFinished = {
                list.remove(item)
                stopIt()
            }
        )
    }

    private fun setForeground() {
        startForeground(Process.myPid(),
            NotificationUtils.buildNotification(this, Const.CHANNEL_ID_DOWNLOAD)
                .setSilent(true)
                .setContentIntent(NotificationUtils.getActivity(MainActivity::class))
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .build()
        )
    }

    private fun notifyFinish(
        id: Int,
        name: String,
        message: String,
        silent: Boolean = false
    ) = NotificationUtils.notify(this, id) {
        setChannelId(Const.CHANNEL_ID_DOWNLOAD)
        setContentTitle(name)
        setContentText(message)
        setSilent(silent)
        setContentIntent(NotificationUtils.getActivity(MainActivity::class))
        setGroup(GROUP_KEY)
        build()
    }

    companion object {
        private const val MODULE_KEY = "ONLINE_MODULE"
        private const val INSTALL_KEY = "INSTALL_MODULE"
        private const val GROUP_KEY = "DOWNLOAD_SERVICE_GROUP_KEY"

        private val progressBroadcast = MutableLiveData<Pair<Float, Module>?>()

        private fun broadcast(progress: Float, module: Module) {
            progressBroadcast.postValue(progress to module)
        }

        fun observeProgress(owner: LifecycleOwner, callback: (Float, Module) -> Unit) {
            progressBroadcast.value = null
            progressBroadcast.observe(owner) {
                val (progress, module) = it ?: return@observe
                callback(progress, module)
            }
        }

        fun start(
            context: Context,
            module: Module,
            install: Boolean
        ) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(MODULE_KEY, module)
                putExtra(INSTALL_KEY, install)
            }
            context.startService(intent)
        }
    }
}