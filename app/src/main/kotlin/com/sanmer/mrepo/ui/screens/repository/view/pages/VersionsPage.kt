package com.sanmer.mrepo.ui.screens.repository.view.pages

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.isLoading
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.ui.component.LabelItem
import com.sanmer.mrepo.ui.component.Loading
import com.sanmer.mrepo.ui.component.MarkdownText
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.utils.expandedShape
import com.sanmer.mrepo.ui.utils.stringRequest
import com.sanmer.mrepo.utils.extensions.toDate
import kotlinx.coroutines.launch

@Composable
fun VersionsPage(
    versions: List<Pair<Repo, VersionItem>>,
    localVersionCode: Int,
    getProgress: @Composable (VersionItem) -> Float,
    onDownload: (VersionItem, Boolean) -> Unit
) = LazyColumn(
    modifier = Modifier.fillMaxSize()
) {
    items(
        items = versions,
        key = { "${it.first.url}, ${it.second.versionCode}" }
    ) { (repo, item) ->
        VersionItem(
            item = item,
            repo = repo,
            localVersionCode = localVersionCode,
            onDownload = onDownload
        )

        val progress = getProgress(item)
        if (progress != 0f) {
            LinearProgressIndicator(
                progress = progress,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
            )
        } else {
            Divider(thickness = 0.9.dp)
        }
    }
}

@Composable
private fun VersionItem(
    item: VersionItem,
    repo: Repo,
    localVersionCode: Int,
    onDownload: (VersionItem, Boolean) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    if (open) VersionItemBottomSheet(
        item = item,
        hasChangelog = item.changelog.isNotBlank(),
        onClose = { open = false },
        onDownload = onDownload
    )

    Row(
        modifier = Modifier
            .clickable(onClick = { open = true })
            .padding(all = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.versionDisplay,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (localVersionCode < item.versionCode) {
                    LabelItem(
                        text = stringResource(id = R.string.module_new)
                            .toUpperCase(Locale.current),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.view_module_provided, repo.name),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = item.timestamp.toDate(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun VersionItemBottomSheet(
    item: VersionItem,
    hasChangelog: Boolean = true,
    state: SheetState = rememberModalBottomSheetState(),
    onDownload: (VersionItem, Boolean) -> Unit,
    onClose: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = state,
    shape = BottomSheetDefaults.expandedShape(15.dp),
    windowInsets = WindowInsets.navigationBars,
    dragHandle = {
        if (hasChangelog) {
            BottomSheetDefaults.DragHandle()
        } else {
            Text(
                modifier = Modifier
                    .padding(all = 18.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.view_module_version_dialog_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
) {
    val userPreferences = LocalUserPreferences.current
    val suState = LocalSuState.current
    val enableInstall by remember {
        derivedStateOf {
            userPreferences.isRoot && suState.isSucceeded
        }
    }

    if (hasChangelog) {
        ButtonRow(
            item = item,
            enableInstall = enableInstall,
            state = state,
            onDownload = onDownload,
            onClose = onClose
        )
        ChangelogItem(url = item.changelog)
    } else {
        ButtonColumn(
            item = item,
            enableInstall = enableInstall,
            state = state,
            downloader = onDownload,
            onClose = onClose
        )
    }
}

@Composable
private fun ColumnScope.ButtonRow(
    item: VersionItem,
    enableInstall: Boolean,
    state: SheetState,
    onDownload: (VersionItem, Boolean) -> Unit,
    onClose: () -> Unit
) = Row(
    modifier = Modifier
        .padding(horizontal = 18.dp)
        .padding(bottom = 18.dp)
        .align(Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    val scope = rememberCoroutineScope()

    OutlinedButton(
        enabled = enableInstall,
        onClick = {
            onDownload(item, true)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.device_mobile_down),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(text = stringResource(id = R.string.module_install))
    }

    OutlinedButton(
        onClick = {
            onDownload(item, false)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.file_download),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(text = stringResource(id = R.string.module_download))
    }
}

@Composable
private fun ChangelogItem(
    url: String
) {
    var changelog by remember { mutableStateOf("") }
    val event = stringRequest(url) { changelog = it }

    Box(
        modifier = Modifier
            .animateContentSize(spring(stiffness = Spring.StiffnessLow))
    ) {
        AnimatedVisibility(
            visible = event.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Loading(minHeight = 200.dp)
        }

        AnimatedVisibility(
            visible = event.isSucceeded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            MarkdownText(
                text = changelog,
                color = AlertDialogDefaults.textContentColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun ColumnScope.ButtonColumn(
    item: VersionItem,
    enableInstall: Boolean,
    state: SheetState,
    downloader: (VersionItem, Boolean) -> Unit,
    onClose: () -> Unit
) = Column(
    modifier = Modifier
        .padding(bottom = 18.dp)
        .align(Alignment.CenterHorizontally),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val scope = rememberCoroutineScope()

    ButtonItem(
        enabled = enableInstall,
        onClick = {
            downloader(item, true)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        icon = R.drawable.device_mobile_down,
        text = stringResource(id = R.string.module_install)
    )

    ButtonItem(
        onClick = {
            downloader(item, false)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        icon = R.drawable.file_download,
        text = stringResource(id = R.string.module_download)
    )
}

@Composable
private fun ButtonItem(
    onClick: () -> Unit,
    enabled: Boolean = true,
    @DrawableRes icon: Int,
    text: String
) = Surface(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
        .alpha(if (enabled) 1f else 0.5f)
        .fillMaxWidth()
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}