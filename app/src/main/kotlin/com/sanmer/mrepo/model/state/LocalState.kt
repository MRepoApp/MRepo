package com.sanmer.mrepo.model.state

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.model.local.LocalModule
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

data class LocalState(
    val lastUpdated: Long?
) {
    companion object {
        suspend fun LocalModule.createState(
            fs: FileSystemManager
        ) = withContext(Dispatchers.IO) {
            val path = "${Const.MODULE_PATH}/${id}"

            val lastUpdated = safe(null) {
                val f1 = fs.getFile("$path/post-fs-data.sh")
                if (f1.exists()) return@safe f1.lastModified()

                val f2 = fs.getFile("$path/service.sh")
                if (f1.exists()) return@safe f2.lastModified()

                val f3 = fs.getFile("$path/system")
                if (f3.exists()) return@safe f3.lastModified()

                val f4 = fs.getFile("$path/module.prop")
                if (f4.exists()) return@safe f3.lastModified()

                return@safe null
            }

            return@withContext LocalState(
                lastUpdated = lastUpdated
            )
        }

        private inline fun <T> safe(default: T, block: () -> T): T {
            return try {
                block()
            } catch (e: Throwable) {
                Timber.e(e)
                default
            }
        }
    }
}
