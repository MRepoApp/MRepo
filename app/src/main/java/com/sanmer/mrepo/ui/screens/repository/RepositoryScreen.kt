package com.sanmer.mrepo.ui.screens.repository

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.data.Repository
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.provider.repo.RepoLoader
import com.sanmer.mrepo.ui.animate.SlideIn
import com.sanmer.mrepo.ui.animate.SlideOut
import com.sanmer.mrepo.ui.component.LinearProgressIndicator
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.expansion.navigateBack
import com.sanmer.mrepo.ui.utils.HtmlText
import com.sanmer.mrepo.ui.utils.NavigateUpTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RepositoryScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val list by remember { derivedStateOf { Repository.repo } }

    BackHandler { navController.navigateBack() }

    var progress by remember { mutableStateOf(false) }
    var add by remember { mutableStateOf(false) }
    if (add) {
        AddDialog(
            onClose = { add = false }
        ) {
            scope.launch(Dispatchers.IO) {
                val repo = Repo(url = it)
                Repository.insert(repo)
                RepoLoader.getRepo(context = context, repo = repo)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            RepositoryTopBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        floatingActionButton = {
            RepositoryFloatingButton { add = true }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (list.isEmpty()){
                PageIndicator(
                    icon = R.drawable.hierarchy_outline,
                    text = R.string.repo_empty
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(
                        items = list,
                        key = { it.id }
                    ) { repo ->
                        RepoItem(
                            repo = repo,
                            onStart = { progress = true },
                            onStop = { progress = false }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = progress,
                enter = SlideIn.topToBottom,
                exit = SlideOut.bottomToTop
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AddDialog(
    onClose: () -> Unit,
    onAdd: (String) -> Unit
) {
    var url by rememberSaveable { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        shape = RoundedCornerShape(25.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.repo_add_dialog_title)) },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge,
                value = url,
                onValueChange = { url = it },
                label = { Text(text = stringResource(id = R.string.repo_add_dialog_label)) },
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Done)
                    focusManager.clearFocus()
                },
                shape = RoundedCornerShape(15.dp),
                supportingText = {
                    HtmlText(
                        text = stringResource(
                            id = R.string.repo_add_dialog_label_support,
                            "<b><a href=\"https://github.com/ya0211/magisk-modules-repo-util\">magisk-modules-repo-util</a></b>"
                        )
                    )
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!url.endsWith("/")) url += "/"

                    onAdd(url)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onClose()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.repo_add_dialog_add)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onClose()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel)
                )
            }
        }
    )
}

@Composable
private fun RepositoryTopBar(
    context: Context = LocalContext.current,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
) = NavigateUpTopBar(
    title = R.string.settings_repo,
    scrollBehavior = scrollBehavior,
    navController = navController,
    actions = {
        val scope = rememberCoroutineScope()
        IconButton(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    Repository.getAll()
                    RepoLoader.getRepoAll(context)
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.rotate_left_outline),
                contentDescription = null
            )
        }
    }
)

@Composable
private fun RepositoryFloatingButton(
    onClick: () -> Unit,
) = FloatingActionButton(
    onClick = onClick
) {
    Icon(
        modifier = Modifier.size(28.dp),
        painter = painterResource(id = R.drawable.add_outline),
        contentDescription = null
    )
}