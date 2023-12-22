package dev.sanmer.mrepo.compat.su

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService
import dev.sanmer.mrepo.compat.BuildConfig
import dev.sanmer.mrepo.compat.impl.ServiceManagerImpl

internal class SuService : RootService() {
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