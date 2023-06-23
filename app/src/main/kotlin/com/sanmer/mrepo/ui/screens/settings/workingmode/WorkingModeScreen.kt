package com.sanmer.mrepo.ui.screens.settings.workingmode

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.utils.navigateBack
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun WorkingModeScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { navController.navigateBack() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WorkingModeItem(
                title = stringResource(id = R.string.setup_root_title),
                desc1 = stringResource(id = R.string.setup_root_desc1),
                desc2 = stringResource(id = R.string.setup_root_desc2),
                selected = userData.isRoot,
                onClick = {
                    viewModel.setWorkingMode(WorkingMode.MODE_ROOT)
                }
            )

            WorkingModeItem(
                title = stringResource(id = R.string.setup_non_root_title),
                desc1 = stringResource(id = R.string.setup_non_root_desc1),
                desc2 = stringResource(id = R.string.setup_non_root_desc2),
                selected = userData.isNonRoot,
                onClick = {
                    viewModel.setWorkingMode(WorkingMode.MODE_NON_ROOT)
                }
            )
        }
    }
}

@Composable
fun WorkingModeItem(
    title: String,
    desc1: String,
    desc2: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) = Box(
    modifier = modifier.fillMaxWidth(0.65f)
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        tonalElevation = if (selected) 2.dp else 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = desc1,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = desc2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (selected) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tick_circle_bold),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_mode),
    scrollBehavior = scrollBehavior,
    navController = navController
)