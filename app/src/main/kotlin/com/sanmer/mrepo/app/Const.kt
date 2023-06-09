package com.sanmer.mrepo.app

import android.os.Environment
import java.io.File

object Const {
    // MODULES
    const val MODULE_PATH = "/data/adb/modules"

    // DIR
    val DIR_PUBLIC_DOWNLOADS: File = Environment
        .getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )

    // URL
    const val TRANSLATE_URL = "https://weblate.sanmer.dev/engage/mrepo"
    const val GITHUB_URL = "https://github.com/ya0211/MRepo"
    const val TELEGRAM_URL = "https://t.me/mrepo_news"
    const val MY_REPO_URL = "https://ya0211.github.io/magisk-modules-repo/"
    const val SPDX_URL = "https://spdx.org/licenses/%s.json"

    // CONTEXT
    const val KSU_CONTEXT = "u:r:su:s0"
    const val MAGISK_CONTEXT = "u:r:magisk:s0"
}