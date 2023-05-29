package com.sanmer.mrepo.utils.expansion

import android.content.Context
import android.content.Intent
import android.net.Uri
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

fun Context.renameDatabase(old: String, new: String) {
    databaseList().forEach {
        if (it.startsWith(old)) {
            val oldFile = getDatabasePath(it)
            val newFile = getDatabasePath(it.replace(old, new))
            oldFile.renameTo(newFile)
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

fun Context.getUriForFile(file: File): Uri {
    return FileProvider.getUriForFile(this,
        "${BuildConfig.APPLICATION_ID}.provider", file
    )
}

fun Context.shareFile(file: File, mimeType: String) {
    ShareCompat.IntentBuilder(this)
        .setType(mimeType)
        .addStream(getUriForFile(file))
        .startChooser()
}

fun Context.navigateToLauncher() {
    val home = Intent(Intent.ACTION_MAIN).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        addCategory(Intent.CATEGORY_HOME)
    }
    startActivity(home)
}