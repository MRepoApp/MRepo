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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object ProviderCompat {
    private var mMode = WorkingMode.FIRST_SETUP
    private val mScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var mProvider: IProvider

    val moduleManager: IModuleManager get() = mProvider.moduleManager
    val fileManager: IFileManager get() = mProvider.fileManager

    var isAlive by mutableStateOf(false)
        private set

    fun init(mode: WorkingMode) {
        if (mode == mMode) {
            if (isAlive) return
        } else {
            if (isAlive) destroy()
        }

        mMode = mode
        when (mMode) {
            WorkingMode.MODE_ROOT -> {
                SuProvider.apply {
                    mProvider = this
                    init()
                }

                SuProvider.isAlive
                    .onEach { isAlive = it }
                    .launchIn(mScope)
            }
            WorkingMode.MODE_SHIZUKU -> {
                ShizukuProvider.apply {
                    mProvider = this
                    init()
                }

                ShizukuProvider.isAlive
                    .onEach { isAlive = it }
                    .launchIn(mScope)
            }
            else -> {}
        }
    }

    fun destroy() = when (mMode) {
        WorkingMode.MODE_ROOT -> SuProvider.destroy()
        WorkingMode.MODE_SHIZUKU -> ShizukuProvider.destroy()
        else -> {}
    }
}