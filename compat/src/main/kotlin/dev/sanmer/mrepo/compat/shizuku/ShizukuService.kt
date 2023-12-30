package dev.sanmer.mrepo.compat.shizuku

import android.content.ComponentName
import dev.sanmer.mrepo.compat.BuildConfig
import dev.sanmer.mrepo.compat.impl.ServiceManagerImpl
import rikka.shizuku.Shizuku

internal class ShizukuService : Shizuku.UserServiceArgs(
    ComponentName(
        BuildConfig.APPLICATION_ID,
        ServiceManagerImpl::class.java.name
    )
) {
    init {
        daemon(false)
        debuggable(BuildConfig.DEBUG)
        version(BuildConfig.VERSION_CODE)
        processNameSuffix("shizuku")
    }
}