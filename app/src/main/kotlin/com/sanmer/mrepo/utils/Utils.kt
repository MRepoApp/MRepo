package com.sanmer.mrepo.utils

import com.topjohnwu.superuser.Shell

object Utils {
    fun reboot(reason: String = "") {
        if (reason == "recovery") {
            // KEYCODE_POWER = 26, hide incorrect "Factory data reset" message
            Shell.cmd("/system/bin/input keyevent 26").submit()
        }
        Shell.cmd("/system/bin/svc power reboot $reason || /system/bin/reboot $reason").submit()
    }

    fun getVersionDisplay(version: String, versionCode: Int): String {
        val included = "\\(.*?${versionCode}.*?\\)".toRegex()
            .containsMatchIn(version)

        return if (included) {
            version
        } else {
            "$version (${versionCode})"
        }
    }

    fun getFilename(name: String, version: String, versionCode: Int, extension: String): String {
        val versionNew = version.replace("\\([^)]*\\)".toRegex(), "")
        return "${name}-${versionNew}-${versionCode}.${extension}"
            .replace("[\\\\/:*?\"<>|]".toRegex(), "")
    }
}