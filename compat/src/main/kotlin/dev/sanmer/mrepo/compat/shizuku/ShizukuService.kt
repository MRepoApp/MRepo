package dev.sanmer.mrepo.compat.shizuku

import android.content.ComponentName
import dev.sanmer.mrepo.compat.Const
import dev.sanmer.mrepo.compat.impl.ServiceManagerImpl
import rikka.shizuku.Shizuku

internal class ShizukuService : Shizuku.UserServiceArgs(
    ComponentName(
        Const.PACKAGE_NAME,
        ServiceManagerImpl::class.java.name
    )
) {
    init {
        daemon(false)
        debuggable(false)
        version(1)
        processNameSuffix("shizuku")
    }
}