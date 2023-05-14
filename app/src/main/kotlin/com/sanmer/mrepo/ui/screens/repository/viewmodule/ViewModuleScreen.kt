package com.sanmer.mrepo.ui.screens.repository.viewmodule

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.utils.navigateBack
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun ViewModuleScreen(
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { navController.navigateBack() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ViewModuleTopBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (viewModel.state.event) {
                Event.LOADING -> {
                    Loading()
                }
                Event.SUCCEEDED -> {
                    ViewModule(
                        isRoot = userData.isRoot,
                        hasChangelog = viewModel.hasChangelog
                    )
                }
                Event.FAILED -> {
                    Failed()
                }
                Event.NON -> {}
            }
        }
    }
}

@Composable
private fun ViewModule(
    isRoot: Boolean,
    hasChangelog: Boolean
) = Column(
    modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(all = 20.dp)
        .fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    ModuleInfoItem()
    ProgressItem()
    InstallButton(isRoot = isRoot)
    VersionsItem(isRoot = isRoot)
    if (hasChangelog) ChangelogItem()
}

@Composable
private fun ProgressItem(
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle(0f)

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
                progress = progress,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ViewModuleTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) = NavigateUpTopBar(
    title = if (viewModel.state.isSucceeded) {
        viewModel.module.name
    } else {
        stringResource(id = R.string.page_view_module)
    },
    scrollBehavior = scrollBehavior,
    navController = navController
)

@Composable
private fun InstallButton(
    isRoot: Boolean,
    context: Context = LocalContext.current,
    viewModel: ModuleViewModel = hiltViewModel()
) = Button(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    contentPadding = PaddingValues(vertical = 12.dp),
    onClick = {
        viewModel.installer(context)
    },
    enabled = isRoot
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
            modifier = Modifier.size(50.dp),
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round
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
    viewModel: ModuleViewModel = hiltViewModel()
) = PageIndicator(
    icon = R.drawable.danger_outline,
    text = viewModel.message ?: stringResource(id = R.string.unknown_error)
)