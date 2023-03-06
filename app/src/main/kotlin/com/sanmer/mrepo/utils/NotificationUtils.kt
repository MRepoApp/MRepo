package com.sanmer.mrepo.utils

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sanmer.mrepo.App
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import kotlin.reflect.KClass

object NotificationUtils {
    val context by lazy { App.context }
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }

    fun init(context: Context) {
        val channels = listOf(
            NotificationChannel(Const.CHANNEL_ID_DOWNLOAD,
                context.getString(R.string.notification_name_download),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        NotificationManagerCompat.from(context)
            .createNotificationChannels(channels)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun PermissionState() {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        LaunchedEffect(permissionState.status) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }

    fun buildNotification(
        context: Context,
        channelId: String
    ) = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo)
        .setSilent(true)

    fun buildNotification(channelId: String) = buildNotification(context, channelId)

    fun notify(
        context: Context,
        notificationId: Int,
        build: NotificationCompat.Builder.() -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = buildNotification(context, "")
        build(notification)
        notificationManager.notify(notificationId, notification.build())
    }

    fun notify(notificationId: Int, notification: Notification) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(notificationId, notification)
    }

    fun notify(
        notificationId: Int,
        build: NotificationCompat.Builder.() -> Unit
    ) = notify(context, notificationId, build)

    fun cancel(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    inline fun <reified T : Activity>getActivity(cls: KClass<T>): PendingIntent {
        val intent = Intent(context, cls.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}