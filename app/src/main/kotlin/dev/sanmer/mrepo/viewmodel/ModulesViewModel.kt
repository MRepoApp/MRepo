package dev.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.content.ThrowableWrapper
import dev.sanmer.mrepo.datastore.model.Homepage
import dev.sanmer.mrepo.datastore.model.ModulesMenu
import dev.sanmer.mrepo.datastore.model.Option
import dev.sanmer.mrepo.model.json.UpdateJson
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.local.State
import dev.sanmer.mrepo.model.online.VersionItem
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.ModulesRepository
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import dev.sanmer.mrepo.service.DownloadService
import dev.sanmer.mrepo.stub.IModuleOpsCallback
import dev.sanmer.mrepo.ui.activity.InstallActivity
import dev.sanmer.mrepo.utils.StrUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val mm get() = Compat.moduleManager
    val isProviderAlive get() = Compat.isAlive

    private val modulesMenu get() = userPreferencesRepository.data
        .map { it.modulesMenu }

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")

    private val cacheFlow = MutableStateFlow(listOf<LocalModule>())
    private val localFlow = MutableStateFlow(listOf<LocalModule>())
    val local get() = localFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set

    private val versionItems = mutableStateMapOf<String, VersionItem?>()

    private val opsTasks = mutableStateListOf<String>()
    private val opsCallback = object : IModuleOpsCallback.Stub() {
        override fun onSuccess(id: String) {
            viewModelScope.launch {
                modulesRepository.getLocal(id)
                opsTasks.remove(id)
            }
        }

        override fun onFailure(id: String, error: ThrowableWrapper) {
            opsTasks.remove(id)
            Timber.e(error.original)
        }
    }

    init {
        Timber.d("ModulesViewModel init")
        providerObserver()
        dataObserver()
        keyObserver()
    }

    private fun providerObserver() {
        Compat.isAliveFlow
            .onEach {
                if (it) getLocalAll()

            }.launchIn(viewModelScope)
    }

    private fun dataObserver() {
        combine(
            localRepository.getLocalAllAsFlow(),
            modulesMenu
        ) { list, menu ->
            cacheFlow.value = list.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { v ->
                if (menu.pinEnabled) {
                    v.sortedByDescending { it.state == State.Enable }
                } else {
                    v
                }
            }

            isLoading = false

        }.launchIn(viewModelScope)
    }

    private fun keyObserver() {
        combine(
            keyFlow,
            cacheFlow
        ) { key, source ->
            localFlow.value = source
                .filter {
                    if (key.isNotBlank()) {
                        it.name.contains(key, ignoreCase = true)
                                || it.author.contains(key, ignoreCase = true)
                                || it.description.contains(key, ignoreCase = true)
                    } else {
                        true
                    }
                }

        }.launchIn(viewModelScope)
    }

    private fun comparator(
        option: Option,
        descending: Boolean
    ): Comparator<LocalModule> = if (descending) {
        when (option) {
            Option.Name -> compareByDescending { it.name.lowercase() }
            Option.UpdatedTime -> compareBy { it.lastUpdated }
        }

    } else {
        when (option) {
            Option.Name -> compareBy { it.name.lowercase() }
            Option.UpdatedTime -> compareByDescending { it.lastUpdated }
        }
    }

    fun search(key: String) {
        keyFlow.value = key
    }

    fun openSearch() {
        isSearch = true
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.value = ""
    }

    private fun getLocalAll() {
        viewModelScope.launch {
            modulesRepository.getLocalAll()
        }
    }

    fun setModulesMenu(value: ModulesMenu) {
        viewModelScope.launch {
            userPreferencesRepository.setModulesMenu(value)
        }
    }

    fun setHomepage() {
        viewModelScope.launch {
            userPreferencesRepository.setHomepage(Homepage.Modules)
        }
    }

    fun createModuleOps(module: LocalModule) = when (module.state) {
        State.Enable -> ModuleOps(
            isOpsRunning = opsTasks.contains(module.id),
            toggle = {
                opsTasks.add(module.id)
                mm.disable(module.id, opsCallback)
            },
            change = {
                opsTasks.add(module.id)
                mm.remove(module.id, opsCallback)
            }
        )

        State.Disable -> ModuleOps(
            isOpsRunning = opsTasks.contains(module.id),
            toggle = {
                opsTasks.add(module.id)
                mm.enable(module.id, opsCallback)
            },
            change = {
                opsTasks.add(module.id)
                mm.remove(module.id, opsCallback)
            }
        )

        State.Remove -> ModuleOps(
            isOpsRunning = opsTasks.contains(module.id),
            toggle = {},
            change = {
                opsTasks.add(module.id)
                mm.enable(module.id, opsCallback)
            }
        )

        State.Update -> ModuleOps(
            isOpsRunning = opsTasks.contains(module.id),
            toggle = {},
            change = {}
        )
    }

    fun getVersionItem(module: LocalModule): VersionItem? {
        viewModelScope.launch {
            if (!localRepository.isUpdatable(module.id)) {
                versionItems.remove(module.id)
                return@launch
            }

            if (versionItems.containsKey(module.id)) {
                return@launch
            }

            val versionItem = if (module.updateJson.isNotBlank()) {
                UpdateJson.load(module.updateJson)
            } else {
                localRepository.getVersionById(module.id).maxByOrNull { it.versionCode }
            }

            versionItems[module.id] = versionItem
        }

        return versionItems[module.id]
    }

    fun downloader(
        context: Context,
        module: LocalModule,
        item: VersionItem,
        install: Boolean
    ) {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.data.first()
            val downloadPath = userPreferences.downloadPath

            val filename = StrUtil.getFilename(
                name = module.name,
                version = item.version,
                versionCode = item.versionCode,
                extension = "zip"
            )

            val task = DownloadService.TaskItem(
                key = item.toString(),
                url = item.zipUrl,
                filename = filename,
                title = module.name,
                desc = item.version
            )

            val listener = object : DownloadService.IDownloadListener {
                override fun onSuccess() {
                    if (install) {
                        InstallActivity.start(
                            context = context,
                            file = File(downloadPath, filename)
                        )
                    }
                }
            }

            DownloadService.start(
                context = context,
                task = task,
                listener = listener
            )
        }
    }

    fun getProgress(item: VersionItem?) =
        DownloadService.getProgressByKey(item.toString())

    data class ModuleOps(
        val isOpsRunning: Boolean,
        val toggle: (Boolean) -> Unit,
        val change: () -> Unit
    )
}