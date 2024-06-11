package dev.sanmer.mrepo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.sanmer.mrepo.datastore.WorkingMode
import dev.sanmer.mrepo.stub.IModuleManager
import dev.sanmer.su.IServiceManager
import dev.sanmer.su.ServiceManagerCompat
import dev.sanmer.su.ServiceManagerCompat.createBy
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

    val moduleManager: IModuleManager by lazy {
        IModuleManager.Stub.asInterface(
            ModuleManager::class.createBy(mService)
        )
    }

    private fun state(): Boolean {
        isAlive = mServiceOrNull != null
        _isAliveFlow.value = isAlive

        return isAlive
    }

    suspend fun init(mode: WorkingMode) = when {
        isAlive -> true
        else -> try {
            mServiceOrNull = when (mode) {
                WorkingMode.Shizuku -> ServiceManagerCompat.fromShizuku()
                WorkingMode.Superuser -> ServiceManagerCompat.fromLibSu()
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
}