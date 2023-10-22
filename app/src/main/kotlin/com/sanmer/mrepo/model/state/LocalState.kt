package com.sanmer.mrepo.model.state

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.model.json.MagiskUpdateJson
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.utils.extensions.totalSize
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

data class LocalState(
    val path: String,
    val lastModified: Long?,
    val size: Long?,
    val updateJson: MagiskUpdateJson?
) {
    companion object {
        suspend fun LocalModule.createState(
            fs: FileSystemManager,
            skipSize: Boolean = false,
            skipJson: Boolean = false
        ) = withContext(Dispatchers.IO) {
            val path = "${Const.MODULE_PATH}/${id}"

            val lastModified = safe(null) {
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

            val size = safe(null) {
                if (skipSize) return@safe null

                val f = fs.getFile(path)
                if (f.exists()) return@safe f.totalSize

                return@safe null
            }

            val updateJson = if (skipJson) {
                null
            } else {
                loadUpdateJson(updateJson)
            }

            return@withContext LocalState(
                path = path,
                lastModified = lastModified,
                size = size,
                updateJson = updateJson
            )
        }

        suspend fun loadUpdateJson(url: String): MagiskUpdateJson? {
            if (!NetworkUtils.isUrl(url)) return null

            return NetworkUtils.requestJson<MagiskUpdateJson>(url)
                .let {
                    if (it.isSuccess) {
                        it.getOrThrow()
                    } else {
                        Timber.e(it.exceptionOrNull(), "updateJson = $url")
                        null
                    }
                }
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
