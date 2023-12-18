package com.sanmer.mrepo.app.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sanmer.mrepo.R

object NotificationUtils {
    const val CHANNEL_ID_DOWNLOAD = "DOWNLOAD"
    const val NOTIFICATION_ID_DOWNLOAD = 1024

    fun init(context: Context) {
        val channels = listOf(
            NotificationChannel(CHANNEL_ID_DOWNLOAD,
                context.getString(R.string.notification_name_download),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        NotificationManagerCompat.from(context).apply {
            createNotificationChannels(channels)
            deleteUnlistedNotificationChannels(channels.map { it.id })
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun PermissionState() {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        SideEffect {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}