package com.sanmer.mrepo.provider.su

import android.content.Context
import com.topjohnwu.superuser.Shell
import timber.log.Timber

class SuShellInitializer : Shell.Initializer() {
    override fun onInit(context: Context, shell: Shell): Boolean {
        Timber.d("isRoot = ${shell.isRoot}")
        return true
    }
}