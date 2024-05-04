package com.sanmer.mrepo.compat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sanmer.mrepo.datastore.WorkingMode
import dev.sanmer.mrepo.compat.ShizukuProvider
import dev.sanmer.mrepo.compat.SuProvider
import dev.sanmer.mrepo.compat.stub.IFileManager
import dev.sanmer.mrepo.compat.stub.IModuleManager
import dev.sanmer.mrepo.compat.stub.IProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object ProviderCompat {
    private val mScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var mProviderOrNull: IProvider? = null
    private val mProvider get() = checkNotNull(mProviderOrNull) {
        "IProvider haven't been received"
    }

    var current by mutableStateOf(WorkingMode.FIRST_SETUP)
        private set
    var isAlive by mutableStateOf(false)
        private set

    private val _isAliveFlow = MutableStateFlow(false)
    val isAliveFlow get() = _isAliveFlow.asStateFlow()

    val moduleManager: IModuleManager get() = mProvider.moduleManager
    val fileManager: IFileManager get() = mProvider.fileManager

    private fun stateObserver(alive: MutableStateFlow<Boolean>) {
        alive.onEach {
            isAlive = it
            _isAliveFlow.value = it

        }.launchIn(mScope)
    }

    private fun init() = when (current) {
        WorkingMode.MODE_SHIZUKU -> with(ShizukuProvider) {
            mProviderOrNull = this
            stateObserver(isAlive)
            init()
        }

        WorkingMode.MODE_ROOT -> with(SuProvider){
            mProviderOrNull = this
            stateObserver(isAlive)
            init()
        }
        else -> {}
    }

    fun init(mode: WorkingMode) {
        when {
            mode == current -> {
                if (!isAlive) init()
            }
            else -> {
                if (isAlive) destroy()

                current = mode
                init()
            }
        }
    }

    fun destroy() = when (current) {
        WorkingMode.MODE_ROOT -> SuProvider.destroy()
        WorkingMode.MODE_SHIZUKU -> ShizukuProvider.destroy()
        else -> {}
    }

    fun <T> get(fallback: T, block: ProviderCompat.() -> T): T {
        return when {
            isAlive -> block(this)
            else -> fallback
        }
    }
}