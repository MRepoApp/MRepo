package com.sanmer.mrepo.compat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.datastore.WorkingMode
import dev.sanmer.mrepo.compat.ShizukuProvider
import dev.sanmer.mrepo.compat.SuProvider
import dev.sanmer.mrepo.compat.stub.IFileManager
import dev.sanmer.mrepo.compat.stub.IModuleManager
import dev.sanmer.mrepo.compat.stub.IServiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

object ProviderCompat {
    private var mServiceOrNull: IServiceManager? = null
    private val mService get() = checkNotNull(mServiceOrNull) {
        "IServiceManager haven't been received"
    }

    var isAlive by mutableStateOf(false)
        private set

    private val _isAliveFlow = MutableStateFlow(false)
    val isAliveFlow get() = _isAliveFlow.asStateFlow()

    val moduleManager: IModuleManager get() = mService.moduleManager
    val fileManager: IFileManager get() = mService.fileManager

    private fun state(alive: Boolean): Boolean {
        isAlive = alive
        _isAliveFlow.value = alive

        return alive
    }

    suspend fun init(mode: WorkingMode) = withContext(Dispatchers.Main) {
        if (isAlive) {
            return@withContext true
        }

        try {
            mServiceOrNull = when (mode) {
                WorkingMode.MODE_SHIZUKU -> ShizukuProvider.launch()
                WorkingMode.MODE_ROOT -> SuProvider.launch()
                else -> null
            }

            state(true)
        } catch (e: Exception) {
            Timber.e(e)

            state(false)
        }
    }

    fun <T> get(fallback: T, block: ProviderCompat.() -> T): T {
        return when {
            isAlive -> block(this)
            else -> fallback
        }
    }
}