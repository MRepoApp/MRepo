package com.sanmer.mrepo.provider

import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.provider.stub.IFileManager
import com.sanmer.mrepo.provider.stub.IModuleManager
import com.sanmer.mrepo.provider.stub.IProvider

object ProviderCompat {
    private var mMode = WorkingMode.FIRST_SETUP
    private lateinit var mProvider: IProvider

    val moduleManager: IModuleManager get() = mProvider.moduleManager
    val fileManager: IFileManager get() = mProvider.fileManager

    val isAlive get() = when {
        ::mProvider.isInitialized -> mProvider.isAlive
        else -> false
    }

    fun init(mode: WorkingMode) {
        if (mode == mMode) {
            return
        } else {
            destroy() // destroy last provider
            mMode = mode
        }

        when (mMode) {
            WorkingMode.MODE_ROOT -> {
                SuProvider.apply {
                    mProvider = this
                    init()
                }
            }
            WorkingMode.MODE_SHIZUKU -> {
                ShizukuProvider.apply {
                    mProvider = this
                    init()
                }
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