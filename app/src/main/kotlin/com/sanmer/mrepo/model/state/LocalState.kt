package com.sanmer.mrepo.model.state

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.utils.extensions.toDateTime
import com.sanmer.mrepo.utils.extensions.totalSize
import com.topjohnwu.superuser.nio.FileSystemManager
import timber.log.Timber
import kotlin.math.log10
import kotlin.math.pow

data class LocalState(
    val path: String,
    val lastModified: String?,
    val size: String?
) {
    companion object {
        fun LocalModule.createState(
            fs: FileSystemManager,
            skipSize: Boolean = false
        ): LocalState {
            val path = "${Const.MODULE_PATH}/${id}"

            val lastModified = safe(null) {
                val f1 = fs.getFile("$path/post-fs-data.sh")
                if (f1.exists()) return@safe f1.lastModified().toDateTime()

                val f2 = fs.getFile("$path/service.sh")
                if (f1.exists()) return@safe f2.lastModified().toDateTime()

                val f3 = fs.getFile("$path/module.prop")
                if (f3.exists()) return@safe f3.lastModified().toDateTime()

                return@safe null
            }

            val size = safe(null) {
                if (skipSize) return@safe null

                val f = fs.getFile(path)
                if (f.exists()) return@safe f.totalSize.formatFileSize()

                return@safe null
            }

            return LocalState(
                path = path,
                lastModified = lastModified,
                size = size
            )
        }

        private fun Long.formatFileSize() = if (this < 0){
            "0 B"
        } else {
            val units = listOf("B", "KB", "MB")
            val group = (log10(toDouble()) / log10(1024.0)).toInt()
            String.format("%.2f %s", this / 1024.0.pow(group.toDouble()), units[group])
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
