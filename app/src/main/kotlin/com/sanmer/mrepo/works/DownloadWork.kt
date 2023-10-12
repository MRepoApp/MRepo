package com.sanmer.mrepo.works

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.di.ApplicationScope
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.utils.HttpUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class DownloadWork @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(
    context,
    workerParams
) {
    private val context: Context by lazy { applicationContext }
    private val progressFlow = MutableStateFlow(0f to workDataOf())

    init {
        progressFlow.sample(500)
            .flowOn(Dispatchers.IO)
            .onEach { (progress, data) ->
                if (data.urlOrNull == null) return@onEach
                updateState(data, progress)
            }
            .launchIn(applicationScope)
    }

    private suspend fun updateState(data: Data, progress: Float) = runCatching {
        setProgress(
            workDataOf(
                PARAM_URL to data.url,
                PARAM_PROGRESS to progress
            )
        )

        setForeground(
            notifyProgress(
                current = (progress * 100).toInt(),
                total = 100,
                title = data.filename
            )
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.url
        val filename = inputData.filename
        val cr = context.contentResolver

        val downloadPath = userPreferencesRepository.data.first().downloadPath
        val path = downloadPath.resolve(filename)

        val output = try {
            downloadPath.apply { if (!exists()) mkdirs() }
            checkNotNull(cr.openOutputStream(path.toUri()))
        } catch (e: Exception) {
            return@withContext onFailure(e)
        }

        val result = HttpUtils.downloader(
            url = url,
            output = output,
            onProgress = { progressFlow.value = it to inputData }
        )

        return@withContext if (result.isSuccess) {
            onSuccess()
        } else {
            onFailure(result.exceptionOrNull())
        }
    }

    private suspend fun onFailure(e: Throwable?): Result {
        val url = inputData.url
        val filename = inputData.filename
        val unknown = context.getString(R.string.unknown_error)

        val message = e?.message ?: unknown
        setForeground(notifyFinish(filename, message, false))

        Timber.e(e, "url = $url, filename = $filename")
        return Result.failure()
    }

    private suspend fun onSuccess(): Result {
        val filename = inputData.filename
        val message = context.getString(R.string.message_download_success)

        setForeground(notifyFinish(filename, message, true))

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return notifyProgress(0, 0)
    }

    private fun notifyProgress(current: Int, total: Int, title: String? = null): ForegroundInfo {
        val channelId = NotificationUtils.CHANNEL_ID_DOWNLOAD
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.launcher_outline)
            .setProgress(total, current, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .apply { title?.let { setContentTitle(it) } }
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                id.version(),
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(id.version(), notification)
        }
    }

    private fun notifyFinish(title: String, message: String, silent: Boolean): ForegroundInfo {
        val channelId = NotificationUtils.CHANNEL_ID_DOWNLOAD
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.launcher_outline)
            .setContentTitle(title)
            .setContentText(message)
            .setSilent(silent)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                id.version(),
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(id.version(), notification)
        }
    }

    @Suppress("unused")
    companion object {
        private const val PARAM_URL = "url"
        private const val PARAM_FILENAME = "filename"
        private const val PARAM_PROGRESS = "progress"
        private val Data.urlOrNull get() = getString(PARAM_URL)
        private val Data.url get() = checkNotNull(getString(PARAM_URL))
        private val Data.filenameOrNull get() = getString(PARAM_FILENAME)
        private val Data.filename get() = checkNotNull(getString(PARAM_FILENAME))
        val Data.progressOrZero get() = getFloat(PARAM_PROGRESS, 0f)

        fun start(
            url: String,
            filename: String
        ) = OneTimeWorkRequestBuilder<DownloadWork>()
            .setInputData(
                workDataOf(
                    PARAM_URL to url,
                    PARAM_FILENAME to filename,
                ),
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
    }
}