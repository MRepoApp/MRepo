package com.sanmer.mrepo.app

import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.sanmer.mrepo.App
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.activity.log.LogActivity

object Shortcut {
    private val context by lazy { App.context }
    private const val ID_LOGS = "logs"
    private const val ID_SETTINGS = "settings"
    private const val ID_MODULES = "modules"
    const val ACTION_MODULES = "${BuildConfig.APPLICATION_ID}.shortcut.modules"
    const val ACTION_SETTINGS = "${BuildConfig.APPLICATION_ID}.shortcut.settings"

    val logs get() = run {
        val activity = Intent(Intent.ACTION_MAIN, null, context, LogActivity::class.java)

        ShortcutInfoCompat.Builder(context, ID_LOGS)
            .setShortLabel(context.getString(R.string.shortcut_log_label))
            .setLongLabel(context.getString(R.string.shortcut_log_label))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_log))
            .setIntent(activity)
            .build()
    }

    val settings get() = run {
        val page = Intent(ACTION_SETTINGS)

        ShortcutInfoCompat.Builder(context, ID_SETTINGS)
            .setShortLabel(context.getString(R.string.shortcut_settings_label))
            .setLongLabel(context.getString(R.string.shortcut_settings_label))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_settings))
            .setIntent(page)
            .build()
    }

    val modules get() = run {
        val page = Intent(ACTION_MODULES)

        ShortcutInfoCompat.Builder(context, ID_MODULES)
            .setShortLabel(context.getString(R.string.shortcut_modules_label))
            .setLongLabel(context.getString(R.string.shortcut_modules_label))
            .setIcon(IconCompat.createWithResource(context, R.drawable.shortcut_modules))
            .setIntent(page)
            .build()
    }

    fun push() {
        ShortcutManagerCompat.pushDynamicShortcut(context, logs)
        ShortcutManagerCompat.pushDynamicShortcut(context, settings)
        ShortcutManagerCompat.pushDynamicShortcut(context, modules)
    }
}