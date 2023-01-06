package com.sanmer.mrepo.app

import android.os.Build
import android.os.Environment
import com.sanmer.mrepo.utils.MediaStoreUtils.toFile
import java.io.File

object Const {
    // DEVICE
    val atLeastS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // MAGISK
    lateinit var MAGISK_VERSION: String
    lateinit var MAGISK_TMP: String
    var isZygiskEnabled = false
    val MAGISK_PATH get() = "$MAGISK_TMP/modules"

    // REPO
    private const val GITHUB_RAW_URL = "https://raw.githubusercontent.com"
    const val REPO_GITHUB = "ya0211/magisk-modules-repo"
    const val REPO_BRANCH = "main"
    val REPO_URL get() = "$GITHUB_RAW_URL/${Config.REPO_GITHUB}/${Config.REPO_BRANCH}/"

    // DIR
    val DIR_PUBLIC_DOWNLOADS: File = Environment
        .getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
    val DOWNLOAD_PATH: File get() = Config.DOWNLOAD_PATH.toFile() ?: DIR_PUBLIC_DOWNLOADS

    // NOTIFICATION
    const val NOTIFICATION_ID_DOWNLOAD = "module_download"
    const val NOTIFICATION_ID_UPDATE = "module_update"
}