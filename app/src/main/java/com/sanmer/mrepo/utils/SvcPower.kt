package com.sanmer.mrepo.utils

import com.topjohnwu.superuser.Shell

object SvcPower {
    fun reboot(reason: String = "") {
        if (reason == "recovery") {
            // KEYCODE_POWER = 26, hide incorrect "Factory data reset" message
            Shell.cmd("/system/bin/input keyevent 26").submit()
        }
        Shell.cmd("/system/bin/svc power reboot $reason || /system/bin/reboot $reason").submit()
    }
}