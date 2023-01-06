package com.sanmer.mrepo.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.app.status.Event
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import timber.log.Timber
import java.io.File

object MagiskUtils {
    var packageName: String? = null
        private set
    var isManagerInstalled = true
        private set

    private val packageNames = listOf(
        "com.topjohnwu.magisk",
        "io.github.vvb2060.magisk",
        "io.github.huskydg.magisk"
    )
    private const val flags = PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES or
            PackageManager.GET_PROVIDERS or PackageManager.GET_RECEIVERS or
            PackageManager.MATCH_DIRECT_BOOT_AWARE or PackageManager.MATCH_DIRECT_BOOT_UNAWARE or
            PackageManager.GET_PERMISSIONS

    fun init() {
        Timber.i("initMagisk")

        ShellHelper.submit(
            command = "magisk --path",
            onCallback = {
                Const.MAGISK_TMP = "$it/.magisk"
            },
            onSucceeded = {
                Const.MAGISK_VERSION = ShellUtils.fastCmd("magisk -c")
                Status.Env.event = Event.SUCCEEDED
                isZygisk()
            },
            onFailed = {
                Status.Env.event = Event.FAILED
                Timber.e("initMagisk (exit code: ${it.code})")
            }
        )
    }

    private fun isZygisk(): Boolean {
        val query = "SELECT value FROM settings WHERE key == \"zygisk\" LIMIT 1"
        val values = Shell.cmd("magisk --sqlite '$query'").exec().out

        Const.isZygiskEnabled = if (values.isNotEmpty()) {
            val map = values.first().split("\\|".toRegex())
                .map { it.split("=", limit = 2) }
                .filter { it.size == 2 }
                .associate { it[0] to it[1] }
            map["value"] == "1"
        } else {
            false
        }

        return Const.isZygiskEnabled
    }

    private fun Context.getStub(): File {
        val stub = cacheDir.resolve("stub.apk")

        if (!stub.exists()) {
            val input = assets.open("stub.apk")
            stub.outputStream().use {
                input.copyTo(it)
            }
        }

        return stub
    }

    private fun PackageManager.getPackageInfo(stubInfo: PackageInfo): PackageInfo? {
        val intent = Intent(Intent.ACTION_MAIN)
        var hidePackageInfo: PackageInfo? = null

        for (pkg in queryActivities(intent, PackageManager.MATCH_ALL)) {
            val pi = packageInfo(pkg.activityInfo.packageName, flags)
            val applicationInfo = pi.applicationInfo
            val apkFile = File(applicationInfo.sourceDir)
            val apkSize = apkFile.length() / 1024

            if (apkSize !in 20..40 && apkSize !in 9 * 1024..20 * 1024) continue
            if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) continue
            if (pi.activities.size != stubInfo.activities.size) continue
            if (pi.services.size != stubInfo.services.size) continue
            if (pi.receivers.size != stubInfo.receivers.size) continue
            if (pi.providers.size != stubInfo.providers.size) continue

            val pPermissionSet = pi.requestedPermissions.toSet()
            val stubPermissionSet = stubInfo.requestedPermissions.toMutableSet()
            stubPermissionSet.remove("com.android.launcher.permission.INSTALL_SHORTCUT")
            if (!pPermissionSet.containsAll(stubPermissionSet)) continue
            hidePackageInfo = pi
        }

        return hidePackageInfo
    }

    fun getManager(context: Context): String? {
        for (p in packageNames) {
            try {
                packageName = context.packageManager.packageInfo(p).packageName
                break
            } catch (e: Exception) {
                Timber.d("No Magisk manager($p) found!")
            }
        }

        try {
            val stub = context.getStub()
            val stubInfo = context.packageManager.packageArchiveInfo(stub.absolutePath, flags)!!
            packageName = context.packageManager.getPackageInfo(stubInfo)?.packageName
        } catch (e: Exception) {
            Timber.d("No hided Magisk manager found!")
            isManagerInstalled = false
        }

        return packageName
    }

    fun launchManager(context: Context) {
        if (!isManagerInstalled) return

        try {
            packageName?.let {
                val intent = context.packageManager.getLaunchIntentForPackage(it)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Timber.d("Failed to launch Magisk manager!")
            getManager(context)
        }
    }
}