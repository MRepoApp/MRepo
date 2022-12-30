package com.sanmer.mrepo.service

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.data.parcelable.Module
import com.sanmer.mrepo.ui.activity.install.InstallActivity
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.utils.HttpUtils
import com.sanmer.mrepo.utils.InstallUtils
import com.sanmer.mrepo.utils.MediaStoreUtils.toFile
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.parcelable
import timber.log.Timber

class DownloadService : LifecycleService() {
    private data class Item(
        val id: Int = System.currentTimeMillis().toInt(),
        val value: Module,
        val isInstall: Boolean
    )
    private val list = mutableListOf<Item>()
    private val context = this

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        setForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand")

        val isInstall = intent?.getBooleanExtra(INSTALL_KEY, false) == true
        val module = intent?.parcelable<Module>(MODULE_KEY)

        if (module == null) {
            Timber.w("MODULE_KEY: null")
            stopIt()
        } else {
            val new = addItem(Item(value = module, isInstall = isInstall))
            Timber.d("new: $new")
            if (new) downloader(list.last())
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun stopIt() = list.isEmpty().let { if (it) stopSelf() }
    private fun addItem(value: Item): Boolean =
        if (value.value in list.map { it.value }) {
            false
        } else {
            list.add(value)
        }

    private fun downloader(
        item: Item
    ) {
        val module = item.value
        val notificationId = item.id
        val notificationIdFinish = item.id + 1

        val path = module.path.toFile() ?: Const.DOWNLOAD_PATH.resolve("${module.name}.zip")
        val notification = NotificationUtils
            .buildNotification(context, Const.NOTIFICATION_ID_DOWNLOAD)
            .setContentTitle(module.name)
            .setContentIntent(NotificationUtils.pendingIntent(context, MainActivity::class))
            .setGroup(GROUP_KEY)

        HttpUtils.downloader(
            url = module.url,
            path = path,
            onProgress = {
                val progress = (it * 100).toInt()

                NotificationUtils.notify(notificationId,
                    notification.setContentText("${progress}%")
                    .setProgress(100, progress, false)
                    .setOngoing(progress != 100)
                    .build()
                )

                if (progress == 100) {
                    NotificationUtils.cancel(notificationId)
                    broadcast(0f, module)
                } else {
                    broadcast(it, module)
                }
            },
            onSuccess = {
                val message = this.getString(R.string.message_download_success)
                notifyByManager(
                    id = notificationIdFinish,
                    name = module.name,
                    message = message,
                    silent = true
                )

                if (item.isInstall) {
                    InstallUtils.install(context, path)

                    val intent = Intent(context, InstallActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            },
            onFail = {
                val message = this.getString(R.string.message_download_failed, it)
                notifyByManager(
                    id = notificationIdFinish,
                    name = module.name,
                    message = message
                )
            },
            onFinish = {
                list.remove(item)
                stopIt()
            }
        )
    }

    private fun setForeground() {
        startForeground(Process.myPid(),
            NotificationUtils.buildNotification(context, Const.NOTIFICATION_ID_DOWNLOAD)
                .setSilent(true)
                .setContentIntent(NotificationUtils.pendingIntent(context, MainActivity::class))
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .build()
        )
    }

    private fun notifyByManager(
        id: Int,
        name: String,
        message: String,
        silent: Boolean = false
    ) = NotificationUtils.notify(context, id) {
        setChannelId(Const.NOTIFICATION_ID_DOWNLOAD)
        setContentTitle(name)
        setContentText(message)
        setSilent(silent)
        setContentIntent(NotificationUtils.pendingIntent(context, MainActivity::class))
        setGroup(GROUP_KEY)
        build()
    }

    companion object {
        const val MODULE_KEY = "ONLINE_MODULE"
        const val INSTALL_KEY = "INSTALL_MODULE"
        const val GROUP_KEY = "com.sanmer.mrepo.service.DOWNLOAD_GROUP_KEY"

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
            isInstall: Boolean
        ) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(MODULE_KEY, module)
            intent.putExtra(INSTALL_KEY, isInstall)
            context.startService(intent)
        }
    }

}