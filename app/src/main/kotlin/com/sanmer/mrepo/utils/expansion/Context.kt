package com.sanmer.mrepo.utils.expansion

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.sanmer.mrepo.BuildConfig
import java.io.File

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

fun Context.toFileDir(input: String?, name: String): File {
    val out = filesDir.resolve(name)
    out.parentFile?.let {
        if (!it.exists()) {
            it.mkdirs()
        }
    }

    val output = out.outputStream()
    output.write(input?.toByteArray())
    output.close()

    return out
}

fun Context.toCacheDir(input: String?, name: String): File {
    val out = cacheDir.resolve(name)
    out.parentFile?.let {
        if (!it.exists()) {
            it.mkdirs()
        }
    }

    val output = out.outputStream()
    output.write(input?.toByteArray())
    output.close()

    return out
}