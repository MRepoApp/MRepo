package com.sanmer.mrepo.app

import android.os.Build
import android.os.Environment
import com.sanmer.mrepo.provider.local.LocalLoader
import com.sanmer.mrepo.provider.local.ModuleUtils
import com.sanmer.mrepo.utils.MediaStoreUtils.toFile
import java.io.File

object Const {
    // DEVICE
    val atLeastS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // MAGISK
    lateinit var MAGISK_PATH: String

    /** Used in [LocalLoader] */
    val MODULES_MOUNT_PATH get() = "$MAGISK_PATH/modules"

    /** Used in [ModuleUtils] */
    const val MODULES_PATH = "/data/adb/modules"

    // DIR
    val DIR_PUBLIC_DOWNLOADS: File = Environment
        .getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
    val DOWNLOAD_PATH: File get() = Config.downloadPath.toFile() ?: DIR_PUBLIC_DOWNLOADS

    // NOTIFICATION
    const val CHANNEL_ID_DOWNLOAD = "module_download"

    // GITHUB
    const val ISSUES_URL = "https://github.com/ya0211/MRepo/issues"

    // REPO
    const val MY_REPO_URL = "https://raw.githubusercontent.com/ya0211/magisk-modules-repo/main/"

    // TELEGRAM
    const val TELEGRAM_CHANNEL_URL = "https://t.me/mrepo_news"
}