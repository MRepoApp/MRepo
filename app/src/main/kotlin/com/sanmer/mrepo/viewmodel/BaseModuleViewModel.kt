package com.sanmer.mrepo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.works.DownloadWork
import com.sanmer.mrepo.works.DownloadWork.Companion.progressOrZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

abstract class BaseModuleViewModel : ViewModel() {

    private val progressFlow = MutableStateFlow("url" to 0f)
    private var prefix = "tmp"

    fun setFilePrefix(name: String) {
        prefix = name
    }

    fun downloader(
        context: Context,
        item: VersionItem,
        onSuccess: (String) -> Unit
    ) {
        val filename = "${prefix}_${item.versionDisplay}.zip"
            .replace("[\\s+|(/)]".toRegex(), "_")

        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork(
            item.zipUrl,
            ExistingWorkPolicy.KEEP,
            DownloadWork.start(url = item.zipUrl, filename = filename),
        )

        workManager.getWorkInfosForUniqueWorkLiveData(item.zipUrl)
            .asFlow()
            .onEach { list ->
                if (list.isEmpty()) return@onEach

                val progress = list.first().progress.progressOrZero
                progressFlow.value = item.zipUrl to progress

                if (list.first().state == WorkInfo.State.SUCCEEDED) {
                    onSuccess(filename)
                }
            }
            .launchIn(viewModelScope)
    }

    suspend fun saveZipFile(
        context: Context,
        zip: File,
        uri: Uri
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            cr.openOutputStream(uri)?.use { output ->
                zip.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }.onSuccess {
            zip.delete()
        }.onFailure {
            Timber.d(it)
        }
    }

    @Composable
    fun rememberProgress(item: VersionItem?): Float {
        val progress by progressFlow.collectAsStateWithLifecycle()
        var value by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(progress) {
            if (item == null) return@LaunchedEffect

            if (progress.first == item.zipUrl) {
                value = progress.second
            }
        }

        return value
    }
}