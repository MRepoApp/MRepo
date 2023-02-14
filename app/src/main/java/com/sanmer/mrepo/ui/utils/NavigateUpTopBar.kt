package com.sanmer.mrepo.ui.utils

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.expansion.navigateBack

@Composable
fun NavigateUpTopBar(
    @StringRes title: Int,
    @StringRes subtitle: Int? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    context: Context = LocalContext.current,
    navController: NavController? = null,
) = NavigateUpTopBar(
    title = {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.titleLarge
            )
            subtitle?.let {
                Text(
                    text = stringResource(id = subtitle),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    },
    actions = actions,
    scrollBehavior = scrollBehavior,
    context = context,
    navController = navController
)

@Composable
fun NavigateUpTopBar(
    title: String,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    context: Context = LocalContext.current,
    navController: NavController? = null,
) = NavigateUpTopBar(
    title = {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            subtitle?.let {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    },
    actions = actions,
    scrollBehavior = scrollBehavior,
    context = context,
    navController = navController
)

@Composable
fun NavigateUpTopBar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    context: Context = LocalContext.current,
    navController: NavController? = null,
) = TopAppBar(
    title = title,
    navigationIcon = {
        IconButton(
            onClick = {
                if (navController != null) {
                    navController.navigateBack()
                } else {
                    val that = context as ComponentActivity
                    that.finish()
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_square_left_outline),
                contentDescription = null
            )
        }
    },
    actions = actions,
    scrollBehavior = scrollBehavior
)