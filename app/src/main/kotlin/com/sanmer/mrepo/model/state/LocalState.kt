package com.sanmer.mrepo.model.state

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.utils.extensions.totalSize
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber

data class LocalState(
    val path: String,
    val lastModified: Long?,
    val size: Long?
) {
    companion object {
        fun LocalModule.createState(
            fs: FileSystemManager,
            skipSize: Boolean = false
        ): LocalState {
            val path = "${Const.MODULE_PATH}/${id}"

            val lastModified = safe(null) {
                val f1 = fs.getFile("$path/post-fs-data.sh")
                if (f1.exists()) return@safe f1.lastModified()

                val f2 = fs.getFile("$path/service.sh")
                if (f1.exists()) return@safe f2.lastModified()

                val f3 = fs.getFile("$path/module.prop")
                if (f3.exists()) return@safe f3.lastModified()

                return@safe null
            }

            val size = safe(null) {
                if (skipSize) return@safe null

                val f = fs.getFile(path)
                if (f.exists()) return@safe f.totalSize

                return@safe null
            }

            return LocalState(
                path = path,
                lastModified = lastModified,
                size = size
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
