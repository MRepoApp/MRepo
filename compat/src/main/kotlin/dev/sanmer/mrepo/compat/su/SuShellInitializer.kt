package dev.sanmer.mrepo.compat.su

import android.content.Context
import android.util.Log
import com.topjohnwu.superuser.Shell

internal class SuShellInitializer : Shell.Initializer() {
    override fun onInit(context: Context, shell: Shell): Boolean {
        Log.d(TAG, "isRoot = ${shell.isRoot}")
        return true
    }

    companion object {
        private const val TAG = "SuShellInitializer"
    }
}