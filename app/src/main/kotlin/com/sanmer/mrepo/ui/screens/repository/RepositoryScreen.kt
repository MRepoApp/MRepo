package com.sanmer.mrepo.ui.screens.repository

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.sanmer.mrepo.ui.component.TopAppBarTitle
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.RepositoryViewModel

@Composable
fun RepositoryScreen(
    navController: NavController,
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.getOnlineAll() }
    )

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::closeSearch
    )

    DisposableEffect(viewModel) {
        onDispose { viewModel.closeSearch() }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                query = viewModel.key,
                onQueryChange = { viewModel.key = it },
                onOpenSearch = { viewModel.isSearch = true },
                onCloseSearch = viewModel::closeSearch,
                scrollBehavior = scrollBehavior
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
    isSearch: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) = if (isSearch) {
    SearchTopBar(
        query = query,
        onQueryChange = onQueryChange,
        onClose = onCloseSearch,
        scrollBehavior = scrollBehavior
    )
} else {
    NormalTopBar(
        onOpenSearch = onOpenSearch,
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun NormalTopBar(
    onOpenSearch: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        TopAppBarTitle(text = stringResource(id = R.string.page_repository))
    },
    actions = {
        IconButton(
            onClick = onOpenSearch
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_normal_outline),
                contentDescription = null
            )
        }

        val context = LocalContext.current
        IconButton(
            onClick = {
                // TODO: Advanced Menu
                Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sort_outline),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)