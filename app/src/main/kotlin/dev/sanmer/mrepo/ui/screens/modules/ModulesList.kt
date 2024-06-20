package dev.sanmer.mrepo.ui.screens.modules

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.local.State
import dev.sanmer.mrepo.model.online.VersionItem
import dev.sanmer.mrepo.ui.component.VersionItemBottomSheet
import dev.sanmer.mrepo.ui.component.scrollbar.VerticalFastScrollbar
import dev.sanmer.mrepo.viewmodel.ModulesViewModel
import kotlinx.coroutines.flow.Flow

@Composable
internal fun ModulesList(
    list: List<LocalModule>,
    state: LazyListState,
    isProviderAlive: Boolean,
    getModuleOps: (LocalModule) -> ModulesViewModel.ModuleOps,
    getVersionItem: (LocalModule) -> VersionItem?,
    getProgress: (VersionItem?) -> Flow<Float>,
    onDownload: (Context, LocalModule, VersionItem, Boolean) -> Unit
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
            key = { it.id }
        ) { module ->
            ModuleItem(
                module = module,
                isProviderAlive = isProviderAlive,
                getModuleOps = getModuleOps,
                getVersionItem = getVersionItem,
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
    isProviderAlive: Boolean,
    getModuleOps: (LocalModule) -> ModulesViewModel.ModuleOps,
    getVersionItem: (LocalModule) -> VersionItem?,
    getProgress: (VersionItem?) -> Flow<Float>,
    onDownload: (Context, LocalModule, VersionItem, Boolean) -> Unit
) {
    val context = LocalContext.current
    val ops by remember(module.state) {
        derivedStateOf { getModuleOps(module) }
    }
    val item by remember {
        derivedStateOf { getVersionItem(module) }
    }

    val progress by getProgress(item).collectAsStateWithLifecycle(initialValue = 0f)

    var open by remember { mutableStateOf(false) }
    if (open && item != null) {
        VersionItemBottomSheet(
            isUpdate = true,
            item = item!!,
            isProviderAlive = isProviderAlive,
            onClose = { open = false },
            onDownload = { onDownload(context, module, item!!, it) }
        )
    }

    ModuleItem(
        module = module,
        progress = progress,
        indeterminate = ops.isOpsRunning,
        alpha = when (module.state) {
            State.Disable, State.Remove -> 0.5f
            else -> 1f
        },
        decoration = when (module.state) {
            State.Remove -> TextDecoration.LineThrough
            else -> TextDecoration.None
        },
        switch = {
            Switch(
                checked = module.state == State.Enable,
                onCheckedChange = ops.toggle,
                enabled = isProviderAlive
            )
        },
        indicator = {
            when (module.state) {
                State.Remove -> StateIndicator(R.drawable.trash)
                State.Update -> StateIndicator(R.drawable.device_mobile_down)
                else -> {}
            }
        },
        trailingButton = {
            item?.let {
                UpdateButton(
                    enabled = it.versionCode > module.versionCode,
                    onClick = { open = true }
                )

                Spacer(modifier = Modifier.width(12.dp))
            }

            RemoveOrRestore(
                module = module,
                enabled = isProviderAlive,
                onClick = ops.change
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
        painter = painterResource(id = if (module.state == State.Remove) {
            R.drawable.rotate
        } else {
            R.drawable.trash
        }),
        contentDescription = null
    )

    Spacer(modifier = Modifier.width(6.dp))
    Text(
        text = stringResource(id = if (module.state == State.Remove) {
            R.string.module_restore
        } else {
            R.string.module_remove
        })
    )
}