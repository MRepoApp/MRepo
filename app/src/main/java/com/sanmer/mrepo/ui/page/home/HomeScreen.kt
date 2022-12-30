package com.sanmer.mrepo.ui.page.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.utils.HtmlText
import com.sanmer.mrepo.ui.utils.Logo

@Composable
fun HomeScreen(
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var show by remember { mutableStateOf(false) }

    if (show) {
        AboutDialog { show = false }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopBar(
                scrollBehavior = scrollBehavior,
                onAbout = { show = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            MagiskItem()
            RepoItem(navController = navController)
            InfoItem()
        }
    }
}

@Composable
private fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onAbout: () -> Unit
) {
    TopAppBar(
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
            IconButton(
                onClick = onAbout
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_link_square_outline),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun AboutDialog(
    onClose: () -> Unit
) {
    AlertDialog(
        shape = RoundedCornerShape(15.dp),
        onDismissRequest = onClose,
        text = {
            Row {

                Logo(
                    modifier = Modifier
                        .size(40.dp),
                    iconRes = R.drawable.ic_logo
                )

                Spacer(modifier = Modifier.width(18.dp))

                Column {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HtmlText(
                        text = stringResource(
                            id = R.string.about_source_code,
                            "<b><a href=\"https://github.com/ya0211/MRepo\">GitHub</a></b>"
                        )
                    )
                }
            }
        },
        confirmButton = { }
    )
}
