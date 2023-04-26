package com.sanmer.mrepo.ui.screens.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.utils.HtmlText
import com.sanmer.mrepo.ui.utils.Logo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current

    BackHandler {
        val home = Intent(Intent.ACTION_MAIN).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            addCategory(Intent.CATEGORY_HOME)
        }
        context.startActivity(home)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopBar(
                userData = userData,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (userData.isRoot) RootItem()
            if (userData.isNonRoot) NonRootItem()
            if (viewModel.isUpdatable) AppUpdateItem()
            InfoItem()
        }
    }
}

@Composable
private fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    userData: UserData
) = TopAppBar(
    title = {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
    },
    navigationIcon = {
        Logo(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(32.dp),
            iconRes = R.drawable.ic_logo
        )
    },
    actions = {
        var expanded by remember { mutableStateOf(false) }

        IconButton(
            onClick = { expanded = true },
            enabled = userData.isRoot
        ) {
            Icon(
                painter = painterResource(id = R.drawable.refresh_outline),
                contentDescription = null
            )

            MenuItem(
                expanded = expanded,
                onClose = { expanded = false }
            )
        }

        var about by remember { mutableStateOf(false) }
        if (about) AboutDialog { about = false }
        IconButton(
            onClick = { about = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.link_square_outline),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun AboutDialog(
    onClose: () -> Unit
) = AlertDialog(
    onDismissRequest = onClose,
) {
    Surface(
        shape = RoundedCornerShape(25.dp),
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Row(
            modifier = Modifier
                .padding(all = 24.dp)
        ) {
            Logo(
                modifier = Modifier
                    .size(50.dp),
                iconRes = R.drawable.ic_logo
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                HtmlText(
                    text = stringResource(
                        id = R.string.about_source_code,
                        "<b><a href=\"https://github.com/ya0211/MRepo\">GitHub</a></b>"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
