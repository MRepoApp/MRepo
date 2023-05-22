package com.sanmer.mrepo.ui.screens.repository.viewmodule.pages

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.isLoading
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.json.VersionItem
import com.sanmer.mrepo.model.json.versionDisplay
import com.sanmer.mrepo.ui.component.Loading
import com.sanmer.mrepo.ui.component.MarkdownText
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.utils.rememberStringDataRequest
import com.sanmer.mrepo.utils.expansion.toDate
import com.sanmer.mrepo.viewmodel.ModuleViewModel
import kotlinx.coroutines.launch

@Composable
fun VersionsPage(
    isRoot: Boolean,
    viewModel: ModuleViewModel = hiltViewModel()
) = Box {
    AnimatedVisibility(
        visible = viewModel.state.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Loading()
    }

    AnimatedVisibility(
        visible = viewModel.state.isSucceeded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        VersionList(
            versions = viewModel.versions,
            isRoot = isRoot
        )
    }

    AnimatedVisibility(
        visible = viewModel.state.isFailed,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        PageIndicator(
            icon = R.drawable.box_outline,
            text = stringResource(id = R.string.search_empty)
        )
    }
}

@Composable
private fun VersionList(
    versions: List<VersionItem>,
    isRoot: Boolean
) = LazyColumn(
    modifier = Modifier.fillMaxSize()
) {
    items(
        items = versions,
        key = { it.versionCode }
    ) {
        VersionItem(
            item = it,
            isRoot = isRoot
        )

        Divider(thickness = 0.9.dp)
    }
}

@Composable
private fun VersionItem(
    item: VersionItem,
    isRoot: Boolean,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val hasChangelog = item.changelog.isNotBlank()
    var repo: Repo? by remember { mutableStateOf(null) }
    LaunchedEffect(item) {
        repo = viewModel.getRepoByUrl(item.repoUrl)
    }

    var show by remember { mutableStateOf(false) }
    if (show && !hasChangelog) {
        VersionItemDialog(
            item = item,
            isRoot = isRoot,
            onClose = { show = false }
        )
    }
    if (show && hasChangelog) {
        VersionItemBottomSheet(
            item = item,
            isRoot = isRoot,
            onClose = { show = false }
        )
    }

    Row(
        modifier = Modifier
            .clickable(onClick = { show = true })
            .padding(all = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.versionDisplay,
                style = MaterialTheme.typography.bodyMedium,
            )

            repo?.let {
                Text(
                    text = stringResource(id = R.string.view_module_provided, it.name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = item.timestamp.toDate(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun VersionItemDialog(
    item: VersionItem,
    isRoot: Boolean,
    viewModel: ModuleViewModel = hiltViewModel(),
    onClose: () -> Unit
) = AlertDialog(
    onDismissRequest = onClose
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.padding(all = 24.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = item.versionDisplay,
                color = AlertDialogDefaults.titleContentColor,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = stringResource(id = R.string.view_module_version_dialog_desc),
                color = AlertDialogDefaults.textContentColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onClose
                ) {
                    Text(text = stringResource(id = R.string.dialog_cancel))
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        viewModel.downloader(
                            context = context,
                            item = item
                        )
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.module_download))
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        viewModel.downloader(
                            context = context,
                            item = item,
                            install = true
                        )
                        onClose()
                    },
                    enabled = isRoot
                ) {
                    Text(text = stringResource(id = R.string.module_install))
                }
            }
        }
    }
}

@Composable
private fun VersionItemBottomSheet(
    item: VersionItem,
    isRoot: Boolean,
    state: SheetState = rememberModalBottomSheetState(),
    viewModel: ModuleViewModel = hiltViewModel(),
    onClose: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = state,
    shape = RoundedCornerShape(15.dp),
    scrimColor = Color.Transparent // TODO: Wait for the windowInsets parameter to be set
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .padding(bottom = 18.dp, start = 18.dp, end = 18.dp)
            .align(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = {
                viewModel.downloader(
                    context = context,
                    item = item,
                    install = true
                )
                scope.launch {
                    onClose()
                    state.hide()
                }
            },
            enabled = isRoot
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
                viewModel.downloader(
                    context = context,
                    item = item
                )
                scope.launch {
                    onClose()
                    state.hide()
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.link_outline),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

            Text(text = stringResource(id = R.string.module_download))
        }
    }

    ChangelogItem(url = item.changelog)

}

@Composable
private fun ChangelogItem(
    url: String
) {
    var changelog by remember { mutableStateOf("") }

    val event = rememberStringDataRequest(url) {
        changelog = it
    }

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
                    .padding(bottom = 18.dp, start = 18.dp, end = 18.dp)
            )
        }
    }
}