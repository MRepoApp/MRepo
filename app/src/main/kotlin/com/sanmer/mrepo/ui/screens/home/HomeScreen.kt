package com.sanmer.mrepo.ui.screens.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.ui.utils.HtmlText
import com.sanmer.mrepo.ui.utils.Logo

@Composable
fun HomeScreen() {
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
            HomeTopBar(scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (EnvProvider.isRoot) RootItem()
            if (EnvProvider.isNonRoot) NonRootItem()
            InfoItem()
        }
    }
}

@Composable
private fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior
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
            enabled = EnvProvider.isRoot
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
                    text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
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
