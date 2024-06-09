package dev.sanmer.mrepo.impl

import dev.sanmer.mrepo.impl.Shell.submit
import dev.sanmer.mrepo.stub.IPowerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PowerManagerImpl(
    private val managerScope: CoroutineScope
) : IPowerManager.Stub() {
    override fun reboot() {
        managerScope.launch {
            "svc power reboot || reboot".submit()
        }
    }
}