package com.sanmer.mrepo.provider.shizuku

import android.content.ComponentName
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.provider.impl.ServiceManagerImpl
import rikka.shizuku.Shizuku

class ShizukuService : Shizuku.UserServiceArgs(
    ComponentName(
        BuildConfig.APPLICATION_ID,
        ServiceManagerImpl::class.java.name
    )
) {
    init {
        daemon(true)
        debuggable(BuildConfig.DEBUG)
        version(BuildConfig.VERSION_CODE)
        processNameSuffix("shizuku")
    }
}