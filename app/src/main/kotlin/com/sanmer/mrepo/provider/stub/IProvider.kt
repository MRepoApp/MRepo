package com.sanmer.mrepo.provider.stub

interface IProvider {
    val uid: Int
    val pid: Int
    val seLinuxContext: String
    val moduleManager: IModuleManager
    val fileManager: IFileManager
    val isKsu: Boolean

    val isAlive: Boolean
    fun init()
    fun destroy()
}