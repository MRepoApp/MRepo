package com.sanmer.mrepo.utils

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.NotificationCompat
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import kotlin.reflect.KClass

object NotificationUtils {
    private lateinit var manager: NotificationManager

    fun init(context: Context) {
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channels = listOf(
            NotificationChannel(Const.NOTIFICATION_ID_DOWNLOAD,
                context.getString(R.string.notification_name_download),
                NotificationManager.IMPORTANCE_HIGH
            ),
            NotificationChannel(Const.NOTIFICATION_ID_UPGRADE,
                context.getString(R.string.notification_name_update),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        manager.createNotificationChannels(channels)
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
        //.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        .setSilent(true)

    fun notify(id: Int, notification: Notification) = manager.notify(id, notification)
    fun cancel(id: Int) = manager.cancel(id)

    fun notify(context: Context, id: Int, build: NotificationCompat.Builder.() -> Unit) {
        val notification = buildNotification(context, "NULL")
        build(notification)
        notify(id, notification.build())
    }

    inline fun <reified T : Activity>pendingIntent(context: Context, cls: KClass<T>): PendingIntent? {
        val intent = Intent(context, cls.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

}