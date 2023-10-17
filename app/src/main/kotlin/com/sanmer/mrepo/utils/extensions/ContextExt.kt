package com.sanmer.mrepo.utils.extensions

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat

val Context.tmpDir get() = cacheDir.resolve("tmp")

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