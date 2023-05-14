package com.sanmer.mrepo.ui.screens.repository

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.component.SearchTopBar
import com.sanmer.mrepo.ui.screens.modules.MenuItem
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.utils.expansion.navigateToLauncher
import com.sanmer.mrepo.viewmodel.RepositoryViewModel

@Composable
fun RepositoryScreen(
    navController: NavController,
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.getOnlineAll() }
    )

    BackHandler {
        if (viewModel.isSearch) {
            viewModel.closeSearch()
        } else {
            context.navigateToLauncher()
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.closeSearch() }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                listState = listState
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .pullRefresh(
                state = pullRefreshState,
                enabled = !viewModel.isSearch
            )
        ) {
            if (viewModel.onlineValue.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.box_outline,
                    text = if (viewModel.isSearch) R.string.search_empty else R.string.repository_empty,
                )
            }

            ModulesList(
                list = viewModel.onlineValue,
                state = listState,
                navController = navController
            )

            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = viewModel.isRefreshing,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                contentColor = MaterialTheme.colorScheme.primary,
                scale = true
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    listState: LazyListState,
    viewModel: RepositoryViewModel = hiltViewModel()
) = if (viewModel.isSearch) {
    SearchTopBar(
        query = viewModel.key,
        onQueryChange = { viewModel.key = it },
        onClose = { viewModel.closeSearch() },
        scrollBehavior = scrollBehavior
    )
} else {
    NormalTopBar(
        scrollBehavior = scrollBehavior,
        listState = listState
    )
}

@Composable
private fun NormalTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    listState: LazyListState,
    viewModel: RepositoryViewModel = hiltViewModel()
) = TopAppBar(
    title = {
        Text(text = stringResource(id = R.string.page_repository))
    },
    actions = {
        IconButton(
            onClick = { viewModel.isSearch = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_normal_outline),
                contentDescription = null
            )
        }

        val context = LocalContext.current
        IconButton(
            onClick = {
                Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sort_outline),
                contentDescription = null
            )
        }

        var expanded by remember { mutableStateOf(false) }
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )

            MenuItem(
                expanded = expanded,
                listState = listState,
                onClose = { expanded = false }
            )
        }
    },
    scrollBehavior = scrollBehavior
)