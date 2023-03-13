package com.sanmer.mrepo.service

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.core.app.ServiceCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.parcelable.DownloadItem
import com.sanmer.mrepo.ui.activity.install.InstallActivity
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.expansion.parcelable
import com.sanmer.mrepo.utils.expansion.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class DownloadService : LifecycleService() {
    val context by lazy { this }
    private val list = mutableListOf<DownloadItem>()

    private fun stopIt() = list.isEmpty().let {
        if (it) lifecycleScope.launch {
            delay(5000)
            if (list.isEmpty()) stopSelf()
        }
    }

    private fun addToList(value: DownloadItem): Boolean =
        if (value.url in list.map { it.url }) {
            false
        } else {
            list.add(value)
        }

    override fun onCreate() {
        super.onCreate()
        setForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val item = intent?.parcelable<DownloadItem>(DOWNLOAD_ITEM)

        item?.let {
            val new = addToList(item)
            if (new) downloader(list.last())
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun downloader(
        item: DownloadItem
    ) = lifecycleScope.launch {
        val notificationId = item.id
        val notificationIdFinish = notificationId + 1

        val path = item.path.toFile()
        Timber.d("download to ${item.path}")

        val notification = NotificationUtils
            .buildNotification(context, Const.CHANNEL_ID_DOWNLOAD)
            .setContentTitle(item.name)
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
                    broadcast(0f, item)
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
                name = item.name,
                message = message,
                silent = true
            )

            if (item.install) {
                if (path.name.endsWith("zip")) {
                    InstallActivity.start(context = context, path = path)
                }

                if (path.name.endsWith("apk")) {
                    runCatching {
                        apkInstall(path)
                    }.onFailure {
                        Timber.e("Install failed: ${it.message}")
                    }
                }
            }
        }

        val failed: (String?) -> Unit = {
            broadcast(0f, item)

            val message = getString(R.string.message_download_failed, it)
            notifyFinish(
                id = notificationIdFinish,
                name = item.name,
                message = message
            )
        }

        HttpUtils.downloader(
            url = item.url,
            out = path,
            onProgress = {
                broadcast(it, item)
                progressFlow.value = (it * 100).toInt()
            }
        ).onSuccess {
            succeeded()

            list.remove(item)
            stopIt()
        }.onFailure {
            failed(it.message)

            list.remove(item)
            stopIt()
        }
    }

    private fun apkInstall(path: File) {
        val apk = cacheDir.resolve("app-release.apk").apply {
            delete()
        }
        contentResolver.openInputStream(path.toUri())!!.use {
            it.copyTo(apk.outputStream())
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val apkUri = FileProvider.getUriForFile(context,
            "${BuildConfig.APPLICATION_ID}.provider", apk)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")

        startActivity(intent)
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
        private const val DOWNLOAD_ITEM = "DOWNLOAD_ITEM"
        private const val GROUP_KEY = "DOWNLOAD_SERVICE_GROUP_KEY"

        private val progressBroadcast = MutableLiveData<Pair<Float, DownloadItem>?>()

        private fun broadcast(progress: Float, item: DownloadItem) {
            progressBroadcast.postValue(progress to item)
        }

        fun observeProgress(owner: LifecycleOwner, callback: (Float, DownloadItem) -> Unit) {
            progressBroadcast.value = null
            progressBroadcast.observe(owner) {
                val (progress, item) = it ?: return@observe
                callback(progress, item)
            }
        }

        fun start(
            context: Context,
            name: String, path: String,
            url: String, install: Boolean
        ) {
            val item = DownloadItem(
                name = name,
                path = path,
                url = url,
                install = install
            )
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(DOWNLOAD_ITEM, item)
            }
            context.startService(intent)
        }
    }
}