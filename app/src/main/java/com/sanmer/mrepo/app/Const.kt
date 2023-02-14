package com.sanmer.mrepo.app

import android.os.Build
import android.os.Environment
import com.sanmer.mrepo.utils.MediaStoreUtils.toFile
import java.io.File

object Const {
    // DEVICE
    val atLeastS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // MAGISK
    lateinit var MAGISK_PATH: String
    val MODULES_PATH get() = "$MAGISK_PATH/modules"

    // REPO
    const val REPO_URL = "https://raw.githubusercontent.com/ya0211/magisk-modules-repo/main/"

    // DIR
    val DIR_PUBLIC_DOWNLOADS: File = Environment
        .getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
    val DOWNLOAD_PATH: File get() = Config.downloadPath.toFile() ?: DIR_PUBLIC_DOWNLOADS

    // NOTIFICATION
    const val NOTIFICATION_ID_DOWNLOAD = "module_download"
    const val NOTIFICATION_ID_UPDATE = "module_update"
}