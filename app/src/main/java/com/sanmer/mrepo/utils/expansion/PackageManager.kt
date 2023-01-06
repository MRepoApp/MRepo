package com.sanmer.mrepo.utils

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU

fun PackageManager.packageInfo(packageName: String, flags: Int = 0): PackageInfo =
    if (SDK_INT >= TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }

fun PackageManager.packageArchiveInfo(archiveFilePath: String, flags: Int = 0): PackageInfo? =
    if (SDK_INT >= TIRAMISU) {
        getPackageArchiveInfo(archiveFilePath, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageArchiveInfo(archiveFilePath, flags)
    }

fun PackageManager.queryActivities(intent: Intent, flags: Int = 0): MutableList<ResolveInfo> =
    if (SDK_INT >= TIRAMISU) {
        queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") queryIntentActivities(intent, flags)
    }