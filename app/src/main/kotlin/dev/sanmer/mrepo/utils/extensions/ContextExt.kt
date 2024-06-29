package dev.sanmer.mrepo.utils.extensions

import android.content.Context
import android.content.Intent
import androidx.core.app.LocaleManagerCompat
import androidx.core.app.ShareCompat

val Context.tmpDir get() = cacheDir.resolve("tmp")
    .apply {
        if (!exists()) mkdirs()
    }

val Context.applicationLocale
    get() = LocaleManagerCompat.getApplicationLocales(applicationContext)
        .toList().firstOrNull()

fun Context.openUrl(url: String) {
    startActivity(
        Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
    )
}

fun Context.shareText(text: String) {
    ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setText(text)
        .startChooser()
}