package dev.sanmer.mrepo.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dev.sanmer.mrepo.R

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
}