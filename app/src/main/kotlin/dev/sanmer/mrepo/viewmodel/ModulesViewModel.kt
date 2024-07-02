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
import dev.sanmer.su.wrap.ThrowableWrapper
import kotlinx.coroutines.flow.Flow
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

    private val cacheFlow = MutableStateFlow(listOf<ModuleWrapper>())
    private val localFlow = MutableStateFlow(listOf<ModuleWrapper>())
    val local get() = localFlow.asStateFlow()

    var isLoading by mutableStateOf(true)
        private set

    private val versionItems = mutableStateMapOf<String, VersionItem?>()

    private val opsTasks = mutableStateListOf<String>()
    private val opsCallback = object : IModuleOpsCallback.Stub() {
        override fun onSuccess(id: String) {
            opsTasks.remove(id)
            viewModelScope.launch {
                modulesRepository.getLocal(id)
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
        ) { local, menu ->
            cacheFlow.value = local.sortedWith(
                comparator(menu.option, menu.descending)
            ).let { list ->
                when {
                    menu.pinEnabled -> list.sortedByDescending { it.state == State.Enable }
                    else -> list
                }
            }.map {
                it.wrap()
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
                .filter { module ->
                    when {
                        key.isBlank() -> true
                        else -> module.original.name.contains(key, ignoreCase = true)
                                || module.original.author.contains(key, ignoreCase = true)
                                || module.original.description.contains(key, ignoreCase = true)
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
                key = item.hashCode(),
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

    private fun getVersionItem(module: LocalModule): VersionItem? {
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

    private fun LocalModule.wrap() = when (state) {
        State.Enable -> ModuleWrapper(
            original = this,
            isOpsRunning = opsTasks.contains(id),
            toggle = {
                opsTasks.add(id)
                mm.disable(id, opsCallback)
            },
            change = {
                opsTasks.add(id)
                mm.remove(id, opsCallback)
            },
            version = getVersionItem(this)
        )
        State.Disable -> ModuleWrapper(
            original = this,
            isOpsRunning = opsTasks.contains(id),
            toggle = {
                opsTasks.add(id)
                mm.enable(id, opsCallback)
            },
            change = {
                opsTasks.add(id)
                mm.remove(id, opsCallback)
            },
            version = getVersionItem(this)
        )
        State.Remove -> ModuleWrapper(
            original = this,
            isOpsRunning = opsTasks.contains(id),
            change = {
                opsTasks.add(id)
                mm.enable(id, opsCallback)
            }
        )
        State.Update -> ModuleWrapper(
            original = this
        )
    }

    data class ModuleWrapper(
        val original: LocalModule,
        val isOpsRunning: Boolean = false,
        val toggle: (Boolean) -> Unit = {},
        val change: () -> Unit = {},
        val version: VersionItem? = null,
        val updatable: Boolean = version?.let { it.versionCode > original.versionCode } == true,
        val progress: Flow<Float> = DownloadService.getProgressByKey(version.hashCode()),
    )
}