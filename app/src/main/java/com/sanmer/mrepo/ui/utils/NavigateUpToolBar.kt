package com.sanmer.mrepo.ui.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.expansion.navigateBack

@Composable
fun NavigateUpToolBar(
    @StringRes title: Int,
    @StringRes subtitle: Int? = null,
    onClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    navController: NavController? = null,
) {
    var click: () -> Unit = onClick
    if (navController != null) {
        click = { navController.navigateBack() }
    }
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleLarge
                )
                if (subtitle != null){
                    Text(
                        text = stringResource(id = subtitle),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = click
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_square_left_outline),
                    contentDescription = null
                )
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior
    )
}