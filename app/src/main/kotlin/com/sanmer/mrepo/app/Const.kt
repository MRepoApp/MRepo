package com.sanmer.mrepo.app

import android.os.Build
import android.os.Environment
import java.io.File

object Const {
    // DEVICE
    val atLeastS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // DIR
    val DIR_PUBLIC_DOWNLOADS: File = Environment
        .getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )

    // NOTIFICATION
    const val CHANNEL_ID_DOWNLOAD = "module_download"

    // URL
    const val TRANSLATE_URL = "https://weblate.sanmer.dev/engage/mrepo/"
    const val ISSUES_URL = "https://github.com/ya0211/MRepo/issues"
    const val TELEGRAM_CHANNEL_URL = "https://t.me/mrepo_news"
    const val MY_REPO_URL = "https://ya0211.github.io/magisk-modules-repo/"
    const val SPDX_URL = "https://spdx.org/licenses/%s.json"
    const val UPDATE_URL = "https://ya0211.github.io/mrepo-files/%s.json"

    // CONTEXT
    const val KSU_CONTEXT = "u:r:su:s0"
    const val MAGISK_CONTEXT = "u:r:magisk:s0"
}