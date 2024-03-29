package dev.sanmer.mrepo.compat.stub

import dev.sanmer.mrepo.compat.impl.Platform
import kotlinx.coroutines.flow.StateFlow

interface IProvider {
    val uid: Int
    val pid: Int
    val seLinuxContext: String
    val moduleManager: IModuleManager
    val fileManager: IFileManager
    val platform: Platform

    val isAlive: StateFlow<Boolean>
    fun init()
    fun destroy()
}