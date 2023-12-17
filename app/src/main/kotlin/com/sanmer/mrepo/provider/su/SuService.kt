package com.sanmer.mrepo.provider.su

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.provider.impl.ServiceManagerImpl
import com.topjohnwu.superuser.ipc.RootService

class SuService : RootService() {
    override fun onBind(intent: Intent): IBinder {
        return ServiceManagerImpl()
    }

    companion object {
        val intent get() = Intent().apply {
            component = ComponentName(
                BuildConfig.APPLICATION_ID,
                SuService::class.java.name
            )

            addCategory(CATEGORY_DAEMON_MODE)
        }
    }
}