package dev.sanmer.mrepo.compat.su

import android.content.Context
import com.topjohnwu.superuser.Shell

internal class SuShellInitializer : Shell.Initializer() {
    override fun onInit(context: Context, shell: Shell) = shell.isRoot
}