package com.sanmer.mrepo.ui.page.modules

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.ui.modify.LinearProgressIndicator
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.module.ModuleLoader
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesScreen(
    viewModel: ModulesViewModel = viewModel()
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        NotificationUtils.PermissionState()
    }

    viewModel.isUpdatable.observe(owner) {
        it?.let { if (it) viewModel.notifyUpdatable(context) }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ModulesTopBar(
                viewModel = viewModel,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            if (Constant.isReady) {
                ModulesList(
                    viewModel = viewModel
                )
            }

            if (!Status.Online.isFinished || !Status.Local.isFinished) {
                if (!Constant.isReady) {
                    Loading()
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

        }
    }
}

@Composable
private fun ModulesList(
    modifier: Modifier = Modifier,
    viewModel: ModulesViewModel = viewModel()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            InstallItem()
        }

        if (viewModel.updatable.isNotEmpty()) {
            item {
                ItemTitle(
                    title = stringResource(id = R.string.modules_title_updates)
                )
            }
            items(
                items = viewModel.updatable,
                key = { it.id }
            ) { item ->
                OnlineModuleItem(
                    viewModel = viewModel,
                    update = true,
                    module = item
                )
            }
        }

        if (viewModel.local.isNotEmpty()) {
            item {
                ItemTitle(
                    title = stringResource(id = R.string.modules_title_installed)
                )
            }
            items(
                items = viewModel.local,
                key = { it.id }
            ) { item ->
                LocalModuleItem(
                    viewModel = viewModel,
                    module = item
                )
            }
        }

        if (viewModel.online.isNotEmpty()) {
            item {
                ItemTitle(
                    title = stringResource(id = R.string.modules_title_online)
                )
            }
            items(
                items = viewModel.online,
                key = { it.id }
            )  { item ->
                OnlineModuleItem(
                    viewModel = viewModel,
                    update = false,
                    module = item
                )
            }
        }
    }
}

@Composable
private fun ModulesTopBar(
    viewModel: ModulesViewModel = viewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.page_modules),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(
                onClick = {
                    ModuleLoader.getAll(context)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_3d_rotate_outline),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ItemTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun Loading() {
    val transition = rememberInfiniteTransition()
    val animateZ by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_box_outline),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(90.dp)
                    .graphicsLayer {
                        rotationZ = animateZ
                    }
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(5.dp)
            )
        }
    }
}
