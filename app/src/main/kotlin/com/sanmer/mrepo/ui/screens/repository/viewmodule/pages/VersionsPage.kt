package com.sanmer.mrepo.ui.screens.repository.viewmodule.pages

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.isLoading
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.ui.component.Loading
import com.sanmer.mrepo.ui.component.MarkdownText
import com.sanmer.mrepo.ui.utils.expandedShape
import com.sanmer.mrepo.ui.utils.rememberStringDataRequest
import com.sanmer.mrepo.utils.extensions.toDate
import kotlinx.coroutines.launch

@Composable
fun VersionsPage(
    versions: List<Pair<Repo, VersionItem>>,
    isRoot: Boolean,
    getProgress: @Composable (VersionItem) -> Float,
    downloader: (Context, VersionItem, Boolean) -> Unit
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
            isRoot = isRoot,
            downloader = downloader
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
            HorizontalDivider(thickness = 0.9.dp)
        }
    }
}

@Composable
private fun VersionItem(
    item: VersionItem,
    repo: Repo,
    isRoot: Boolean,
    downloader: (Context, VersionItem, Boolean) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    if (open) VersionItemBottomSheet(
        item = item,
        isRoot = isRoot,
        hasChangelog = item.changelog.isNotBlank(),
        onClose = { open = false },
        downloader = downloader
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
            Text(
                text = item.versionDisplay,
                style = MaterialTheme.typography.bodyMedium
            )

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
    isRoot: Boolean,
    hasChangelog: Boolean = true,
    state: SheetState = rememberModalBottomSheetState(),
    downloader: (Context, VersionItem, Boolean) -> Unit,
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
    if (hasChangelog) {
        ButtonRow(
            item = item,
            isRoot = isRoot,
            state = state,
            downloader = downloader,
            onClose = onClose
        )
        ChangelogItem(url = item.changelog)
    } else {
        ButtonColumn(
            item = item,
            isRoot = isRoot,
            state = state,
            downloader = downloader,
            onClose = onClose
        )
    }
}

@Composable
private fun ColumnScope.ButtonRow(
    item: VersionItem,
    isRoot: Boolean,
    state: SheetState,
    downloader: (Context, VersionItem, Boolean) -> Unit,
    onClose: () -> Unit
) = Row(
    modifier = Modifier
        .padding(horizontal = 18.dp)
        .padding(bottom = 18.dp)
        .align(Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            downloader(context, item, true)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        enabled = isRoot,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.import_outline),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(text = stringResource(id = R.string.module_install))
    }

    OutlinedButton(
        onClick = {
            downloader(context, item, false)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.link_outline),
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
    val event = rememberStringDataRequest(url) { changelog = it }

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
    isRoot: Boolean,
    state: SheetState,
    downloader: (Context, VersionItem, Boolean) -> Unit,
    onClose: () -> Unit
) = Column(
    modifier = Modifier
        .padding(bottom = 18.dp)
        .align(Alignment.CenterHorizontally),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ButtonItem(
        onClick = {
            downloader(context, item, true)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        enabled = isRoot,
        icon = R.drawable.import_outline,
        text = stringResource(id = R.string.module_install)
    )

    ButtonItem(
        onClick = {
            downloader(context, item, false)
            scope.launch {
                onClose()
                state.hide()
            }
        },
        icon = R.drawable.link_outline,
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
    modifier = Modifier.fillMaxWidth()
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
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