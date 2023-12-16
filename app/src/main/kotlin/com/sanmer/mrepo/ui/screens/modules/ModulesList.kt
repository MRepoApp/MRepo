package com.sanmer.mrepo.ui.screens.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.json.UpdateJson
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.model.state.LocalState
import com.sanmer.mrepo.ui.component.VersionItemBottomSheet
import com.sanmer.mrepo.ui.component.scrollbar.VerticalFastScrollbar
import com.sanmer.mrepo.viewmodel.ModulesViewModel.Companion.LocalUiState

@Composable
fun ModulesList(
    list: List<Pair<LocalState, LocalModule>>,
    state: LazyListState,
    isProviderAlive: Boolean,
    getUiState: @Composable (LocalModule) -> LocalUiState,
    getUpdateJson: @Composable (LocalModule) -> UpdateJson?,
    getProgress: @Composable (VersionItem?) -> Float,
    onDownload: (String, VersionItem, Boolean) -> Unit
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = list,
            key = { it.second.id }
        ) { (state, module) ->
            ModuleItem(
                module = module,
                state = state,
                isProviderAlive = isProviderAlive,
                getUiState = getUiState,
                getUpdateJson = getUpdateJson,
                getProgress = getProgress,
                onDownload = onDownload
            )
        }
    }

    VerticalFastScrollbar(
        state = state,
        modifier = Modifier.align(Alignment.CenterEnd)
    )
}

@Composable
fun ModuleItem(
    module: LocalModule,
    state: LocalState,
    isProviderAlive: Boolean,
    getUiState: @Composable (LocalModule) -> LocalUiState,
    getUpdateJson: @Composable (LocalModule) -> UpdateJson?,
    getProgress: @Composable (VersionItem?) -> Float,
    onDownload: (String, VersionItem, Boolean) -> Unit
) {
    val uiState = getUiState(module)
    val updateJson = getUpdateJson(module)
    val item by remember(updateJson) { derivedStateOf { updateJson?.toItemOrNull() } }
    val progress = getProgress(item)

    var open by remember { mutableStateOf(false) }
    if (open && item != null) {
        VersionItemBottomSheet(
            isUpdate = true,
            item = item!!,
            isProviderAlive = isProviderAlive,
            onClose = { open = false },
            onDownload = { onDownload(module.name, item!!, it) }
        )
    }

    ModuleItem(
        module = module,
        state = state,
        progress = progress,
        alpha = uiState.alpha,
        decoration = uiState.decoration,
        switch = {
            Switch(
                checked = module.state == State.ENABLE,
                onCheckedChange = uiState.toggle,
                enabled = isProviderAlive
            )
        },
        indicator = when (module.state) {
            State.REMOVE -> stateIndicator(R.drawable.trash)
            State.UPDATE -> stateIndicator(R.drawable.device_mobile_down)
            else -> null
        },
        trailingButton = {
            if (updateJson != null) {
                UpdateButton(
                    enabled = updateJson.versionCode > module.versionCode,
                    onClick = { open = true }
                )

                Spacer(modifier = Modifier.width(12.dp))
            }

            RemoveOrRestore(
                module = module,
                enabled = isProviderAlive,
                onClick = uiState.change
            )
        }
    )
}

@Composable
private fun UpdateButton(
    enabled: Boolean,
    onClick: () -> Unit
) = FilledTonalButton(
    onClick = onClick,
    enabled = enabled,
    contentPadding = PaddingValues(horizontal = 12.dp)
) {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = R.drawable.device_mobile_down),
        contentDescription = null
    )

    Spacer(modifier = Modifier.width(6.dp))
    Text(
        text = stringResource(id = R.string.module_update)
    )
}

@Composable
private fun RemoveOrRestore(
    module: LocalModule,
    enabled: Boolean,
    onClick: () -> Unit
) = FilledTonalButton(
    onClick = onClick,
    enabled = enabled,
    contentPadding = PaddingValues(horizontal = 12.dp)
) {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = if (module.state == State.REMOVE) {
            R.drawable.rotate
        } else {
            R.drawable.trash
        }),
        contentDescription = null
    )

    Spacer(modifier = Modifier.width(6.dp))
    Text(
        text = stringResource(id = if (module.state == State.REMOVE) {
            R.string.module_restore
        } else {
            R.string.module_remove
        })
    )
}