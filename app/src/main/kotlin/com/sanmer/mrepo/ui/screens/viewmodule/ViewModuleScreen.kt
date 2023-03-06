package com.sanmer.mrepo.ui.screens.viewmodule

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Event
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.ui.component.CircularProgressIndicator
import com.sanmer.mrepo.ui.component.LinearProgressIndicator
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.utils.NavigateUpTopBar
import com.sanmer.mrepo.utils.expansion.navigateBack
import com.sanmer.mrepo.viewmodel.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ViewModuleScreen(
    viewModel: DetailViewModel = viewModel(),
    navController: NavController,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { navController.navigateBack() }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ViewModuleTopBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (viewModel.state.event) {
                Event.LOADING -> {
                    Loading()
                }
                Event.SUCCEEDED -> {
                    ViewModule()
                }
                Event.FAILED -> {
                    Failed()
                }
                Event.NON -> {
                    Failed()
                }
            }
        }
    }
}

@Composable
private fun ViewModule(
    viewModel: DetailViewModel = viewModel(),
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModuleInfoItem()
        ProgressItem()
        InstallButton()
        VersionsItem()
        if (viewModel.hasChangelog) ChangelogItem()
    }
}

@Composable
private fun ProgressItem(
    viewModel: DetailViewModel = viewModel()
) {
    val owner = LocalLifecycleOwner.current
    var progress by remember { mutableStateOf(0f) }
    viewModel.observeProgress(owner) { progress = it }

    if (0f < progress && progress < 1f) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                progress = progress
            )
        }
    }
}

@Composable
private fun ViewModuleTopBar(
    viewModel: DetailViewModel = viewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = NavigateUpTopBar(
    title = R.string.page_view_module,
    actions = {
        val scope = rememberCoroutineScope()
        IconButton(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    viewModel.state.setLoading()
                    viewModel.getUpdates()
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.rotate_left_outline),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior,
    navController = navController
)

@Composable
private fun InstallButton(
    context: Context = LocalContext.current,
    viewModel: DetailViewModel = viewModel()
) = Button(
    modifier = Modifier
        .fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    contentPadding = PaddingValues(vertical = 12.dp),
    onClick = {
        viewModel.installer(context)
    },
    enabled = EnvProvider.isRoot
) {
    Text(
        text = stringResource(id = R.string.module_install),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun Loading() = PageIndicator(
    icon = {
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp),
            strokeWidth = 5.dp
        )
    },
    text = {
        Text(
            text = stringResource(id = R.string.message_loading),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
)

@Composable
private fun Failed(
    viewModel: DetailViewModel = viewModel()
) = PageIndicator(
    icon = R.drawable.danger_outline,
    text = viewModel.message ?: stringResource(id = R.string.unknown_error)
)