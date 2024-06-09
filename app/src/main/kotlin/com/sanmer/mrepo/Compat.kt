package com.sanmer.mrepo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.datastore.WorkingMode
import dev.sanmer.mrepo.ManagerService.Companion.managerService
import dev.sanmer.mrepo.stub.IFileManager
import dev.sanmer.mrepo.stub.IModuleManager
import dev.sanmer.mrepo.stub.IPowerManager
import dev.sanmer.su.IServiceManager
import dev.sanmer.su.ServiceManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

object Compat {
    private var mServiceOrNull: IServiceManager? = null
    private val mService get() = checkNotNull(mServiceOrNull) {
        "IServiceManager haven't been received"
    }

    var isAlive by mutableStateOf(false)
        private set

    private val _isAliveFlow = MutableStateFlow(false)
    val isAliveFlow get() = _isAliveFlow.asStateFlow()

    private fun state(): Boolean {
        isAlive = mServiceOrNull != null
        _isAliveFlow.value = isAlive

        return isAlive
    }

    suspend fun init(mode: WorkingMode) = when {
        isAlive -> true
        else -> try {
            mServiceOrNull = when (mode) {
                WorkingMode.MODE_SHIZUKU -> ServiceManagerCompat.fromShizuku()
                WorkingMode.MODE_ROOT -> ServiceManagerCompat.fromLibSu()
                else -> null
            }

            state()
        } catch (e: Exception) {
            Timber.e(e)

            mServiceOrNull = null
            state()
        }
    }

    fun <T> get(fallback: T, block: Compat.() -> T): T {
        return when {
            isAlive -> block(this)
            else -> fallback
        }
    }

    fun getModuleManager(): IModuleManager = mService.managerService.moduleManager
    fun getFileManager(): IFileManager = mService.managerService.fileManager
    fun getPowerManager(): IPowerManager = mService.managerService.powerManager
}