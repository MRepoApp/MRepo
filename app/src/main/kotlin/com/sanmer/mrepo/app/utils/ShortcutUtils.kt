package com.sanmer.mrepo.app.utils

import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.sanmer.mrepo.App
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.activity.log.LogActivity

object ShortcutUtils {
    private val context by lazy { App.context }
    private const val ID_LOGS = "logs"

    init {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }

    private val logs get() = run {
        val activity = Intent(Intent.ACTION_MAIN, null, context, LogActivity::class.java)

        ShortcutInfoCompat.Builder(context, ID_LOGS)
            .setShortLabel(context.getString(R.string.shortcut_log_label))
            .setLongLabel(context.getString(R.string.shortcut_log_label))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_log))
            .setIntent(activity)
            .build()
    }

    fun push() {
        ShortcutManagerCompat.pushDynamicShortcut(context, logs)
    }
}