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
            var lastUpdated: Long? = null
            Const.MODULE_FILES.first { filename ->
                val file = fs.getFile("${Const.MODULES_PATH}/${id}/${filename}")
                if (file.exists()) {
                    lastUpdated = file.lastModified()
                    true
                } else {
                    false
                }
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
