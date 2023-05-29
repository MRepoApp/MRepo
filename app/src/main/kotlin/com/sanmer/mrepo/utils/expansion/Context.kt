package com.sanmer.mrepo.utils.expansion

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.sanmer.mrepo.BuildConfig
import java.io.File

val Context.logDir get() = cacheDir.resolve("log")

fun Context.deleteLog(name: String) {
    logDir.listFiles().orEmpty()
        .forEach {
            if (it.name.startsWith(name) && it.isFile) {
                it.delete()
            }
        }
}

fun Context.openUrl(url: String) {
    Intent.parseUri(url, Intent.URI_INTENT_SCHEME).apply {
        startActivity(this)
    }
}

fun Context.shareText(text: String) {
    ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setText(text)
        .startChooser()
}

fun Context.shareFile(file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(this,
        "${BuildConfig.APPLICATION_ID}.provider", file)

    ShareCompat.IntentBuilder(this)
        .setType(mimeType)
        .addStream(uri)
        .startChooser()
}

fun Context.navigateToLauncher() {
    val home = Intent(Intent.ACTION_MAIN).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        addCategory(Intent.CATEGORY_HOME)
    }
    startActivity(home)
}