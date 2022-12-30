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

    private fun getStub(context: Context): File {
        val stub = context.cacheDir.resolve("stub.apk")

        if (!stub.exists()) {
            val input = context.assets.open("stub.apk")
            stub.outputStream().use {
                input.copyTo(it)
            }
        }

        return stub
    }

    private fun getStubInfo(context: Context): PackageInfo? {
        val stub = getStub(context)
        return context.packageManager.packageArchiveInfo(stub.absolutePath, flags)
    }

    private fun getPI(context: Context, packageName: String): PackageInfo {
        return context.packageManager.packageInfo(packageName)
    }

    private fun getPIHided(context: Context): PackageInfo? {
        val stubInfo = getStubInfo(context)!!
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        var hidePackageInfo: PackageInfo? = null

        for (pkg in pm.queryActivities(intent, PackageManager.MATCH_ALL)) {
            runCatching {
                val pi = pm.packageInfo(pkg.activityInfo.packageName, flags)
                val applicationInfo = pi.applicationInfo
                val apkFile = File(applicationInfo.sourceDir)
                val apkSize = apkFile.length() / 1024

                if (apkSize !in 20..40 && apkSize !in 9 * 1024..20 * 1024) return@runCatching
                if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) return@runCatching
                if (pi.activities.size != stubInfo.activities.size) return@runCatching
                if (pi.services.size != stubInfo.services.size) return@runCatching
                if (pi.receivers.size != stubInfo.receivers.size) return@runCatching
                if (pi.providers.size != stubInfo.providers.size) return@runCatching

                val pPermissionSet = pi.requestedPermissions.toSet()
                val stubPermissionSet = stubInfo.requestedPermissions.toMutableSet()
                stubPermissionSet.remove("com.android.launcher.permission.INSTALL_SHORTCUT")
                if (!pPermissionSet.containsAll(stubPermissionSet)) return@runCatching
                hidePackageInfo = pi
            }
        }

        return hidePackageInfo
    }

    fun getApp(context: Context): String? {
        for (p in packageNames) {
            try {
                packageName = getPI(context, p).packageName
                break
            } catch (e: Exception) {
                Timber.d("No Magisk manager($p) found!")
            }
        }

        try {
            packageName = getPIHided(context)?.packageName
        } catch (e: Exception) {
            Timber.d("No hided Magisk manager found!")
        }

        return packageName
    }

    fun launchApp(context: Context) = try {
        packageName?.let {
            val intent = context.packageManager.getLaunchIntentForPackage(it)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        Timber.d("Failed to launch Magisk manager!")
        getApp(context)
    }
}