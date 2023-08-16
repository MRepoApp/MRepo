package com.sanmer.mrepo.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.model.state.LocalState.Companion.createState
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.ModulesRepository
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.utils.ModuleUtils
import com.topjohnwu.superuser.nio.FileSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val modulesRepository: ModulesRepository,
    private val suRepository: SuRepository
) : ViewModel() {
    val suState get() = suRepository.state
    private val fs get() = try {
        suRepository.fs
    } catch (e: Exception) {
        FileSystemManager.getLocal()
    }

    var isSearch by mutableStateOf(false)
    var key by mutableStateOf("")

    private val local get() = localRepository.local
        .map { local ->
            local.createState(
                fs = fs,
                skipSize = true
            ) to local
        }.sortedBy { (_, module) ->
            module.name
        }

    private val localSearch by derivedStateOf {
        local.filter { (_, module) ->
            if (key.isBlank()) return@filter true
            key.uppercase() in "${module.name}${module.author}".uppercase()
        }
    }

    val localValue get() = (if (isSearch) localSearch else local)

    var isRefreshing by mutableStateOf(false)
        private set
    private inline fun <T> T.refreshing(callback: T.() -> Unit) {
        isRefreshing = true
        callback()
        isRefreshing = false
    }

    init {
        Timber.d("ModulesViewModel init")
    }

    fun closeSearch() {
        isSearch = false
        key = ""
    }

    fun getLocalAll() = viewModelScope.launch {
        refreshing {
            modulesRepository.getLocalAll()
        }
    }

    @Stable
    data class UiState(
        val alpha: Float = 1f,
        val decoration: TextDecoration = TextDecoration.None,
        val toggle: (Boolean) -> Unit = {},
        val change: () -> Unit = {},
        val manager: (() -> Unit)? = null
    )

    private fun createUiState(
        context: Context,
        module: LocalModule
    ): UiState = when (module.state) {
        State.ENABLE -> UiState(
            alpha = 1f,
            decoration = TextDecoration.None,
            toggle = { suRepository.disable(module) },
            change = { suRepository.remove(module) },
            manager = ModuleUtils.launchManger(context, module)
        )

        State.DISABLE -> UiState(
            alpha = 0.5f,
            toggle = { suRepository.enable(module) },
            change = { suRepository.remove(module) },
            manager = ModuleUtils.launchManger(context, module)
        )

        State.REMOVE -> UiState(
            alpha = 0.5f,
            decoration = TextDecoration.LineThrough,
            change = { suRepository.enable(module) }
        )
        State.ZYGISK_UNLOADED,
        State.RIRU_DISABLE,
        State.ZYGISK_DISABLE -> UiState(
            alpha = 0.5f
        )
        State.UPDATE -> UiState()
    }

    @Composable
    fun rememberUiState(module: LocalModule): UiState {
        val context = LocalContext.current

        return remember(key1 = module.state, key2 = isRefreshing) {
            createUiState(context, module)
        }
    }
}