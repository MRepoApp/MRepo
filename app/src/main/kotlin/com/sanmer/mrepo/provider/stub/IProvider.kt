package com.sanmer.mrepo.provider.stub

import com.topjohnwu.superuser.nio.FileSystemManager

interface IProvider {
    val uid: Int
    val pid: Int
    val seLinuxContext: String
    val moduleManager: IModuleManager
    val fileSystemManager: FileSystemManager

    val isAlive: Boolean
    fun init()
    fun destroy()
}