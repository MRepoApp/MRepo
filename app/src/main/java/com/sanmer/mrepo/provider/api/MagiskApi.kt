package com.sanmer.mrepo.provider.api

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Status
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import timber.log.Timber

object MagiskApi {
    private var version: String = "Magisk"
    private var isZygiskEnabled = false

    fun getVersion() = version
    fun isZygiskEnabled() = isZygiskEnabled

    fun init() {
        Timber.i("initMagisk")
        Shell.cmd("magisk --path").submit {
            val output = it.out.joinToString().trim()
            if (it.isSuccess) {
                Const.MAGISK_PATH = "$output/.magisk"
                version = ShellUtils.fastCmd("magisk -c")
                isZygiskEnabled = isZygisk()
                Status.Env.setSucceeded()
            } else {
                Status.Env.setFailed()
                Timber.e("initMagisk: $output")
            }
        }
    }

    private fun isZygisk(): Boolean {
        val query = "SELECT value FROM settings WHERE key == \"zygisk\" LIMIT 1"
        val values = Shell.cmd("magisk --sqlite '$query'").exec().out

        return if (values.isNotEmpty()) {
            val map = values.first().split("\\|".toRegex())
                .map { it.split("=", limit = 2) }
                .filter { it.size == 2 }
                .associate { it[0] to it[1] }
            map["value"] == "1"
        } else {
            false
        }
    }
}