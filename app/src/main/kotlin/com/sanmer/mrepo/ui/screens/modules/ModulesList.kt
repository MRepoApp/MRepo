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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State
import com.sanmer.mrepo.model.state.LocalState
import com.sanmer.mrepo.ui.component.scrollbar.VerticalFastScrollbar
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesList(
    list: List<Pair<LocalState, LocalModule>>,
    state: LazyListState,
    suState: Event,
    getUiState: @Composable (LocalModule) -> ModulesViewModel.UiState
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
                suState = suState,
                getUiState = getUiState
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
    suState: Event,
    getUiState: @Composable (LocalModule) -> ModulesViewModel.UiState
) {
    val uiState = getUiState(module)

    ModuleItem(
        module = module,
        state = state,
        alpha = uiState.alpha,
        decoration = uiState.decoration,
        switch = {
            Switch(
                checked = module.state == State.ENABLE,
                onCheckedChange = uiState.toggle,
                enabled = suState.isSucceeded
            )
        },
        indicator = when (module.state) {
            State.REMOVE -> stateIndicator(R.drawable.trash_outline)
            State.UPDATE -> stateIndicator(R.drawable.import_outline)
            State.ZYGISK_UNLOADED,
            State.RIRU_DISABLE,
            State.ZYGISK_DISABLE -> stateIndicator(R.drawable.danger_outline)
            else -> null
        },
        leadingButton = {
            if (uiState.manager != null) {
                Manager(uiState.manager)
            }
        },
        trailingButton = {
            RemoveOrRestore(
                module = module,
                onClick = uiState.change,
                enabled = suState.isSucceeded
            )
        }
    )
}

@Composable
private fun RemoveOrRestore(
    module: LocalModule,
    onClick: () -> Unit,
    enabled: Boolean
) = FilledTonalButton(
    onClick = onClick,
    enabled = enabled,
    contentPadding = PaddingValues(horizontal = 12.dp)
) {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = if (module.state == State.REMOVE) {
            R.drawable.refresh_outline
        } else {
            R.drawable.trash_outline
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

@Composable
private fun Manager(
    onClick: () -> Unit
) = FilledTonalIconButton(
    onClick = onClick
) {
    Icon(
        painter = painterResource(id = R.drawable.setting_outline),
        contentDescription = null,
    )
}