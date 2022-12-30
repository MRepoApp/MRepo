package com.sanmer.mrepo.utils

import com.topjohnwu.superuser.Shell

object SvcPower {
    fun reboot() {
        Shell.cmd("svc power reboot").submit()
    }
}