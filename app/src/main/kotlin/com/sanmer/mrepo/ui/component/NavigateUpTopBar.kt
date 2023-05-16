package com.sanmer.mrepo.ui.component

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.utils.navigateBack

@Composable
fun NavigateUpTopBar(
    @StringRes title: Int,
    @StringRes subtitle: Int? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    context: Context = LocalContext.current,
    navController: NavController? = null,
    enable: Boolean = true
) = NavigateUpTopBar(
    title = stringResource(id = title),
    subtitle = if (subtitle != null) stringResource(id = subtitle) else null,
    actions = actions,
    scrollBehavior = scrollBehavior,
    context = context,
    navController = navController,
    enable = enable
)

@Composable
fun NavigateUpTopBar(
    title: String,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    context: Context = LocalContext.current,
    navController: NavController? = null,
    enable: Boolean = true
) = NavigateUpTopBar(
    title = {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            subtitle?.let {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    },
    actions = actions,
    scrollBehavior = scrollBehavior,
    context = context,
    navController = navController,
    enable = enable
)

@Composable
fun NavigateUpTopBar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    context: Context = LocalContext.current,
    navController: NavController? = null,
    enable: Boolean = true,
) = TopAppBar(
    title = title,
    navigationIcon = {
        IconButton(
            onClick = {
                if (!enable) return@IconButton

                if (navController != null) {
                    navController.navigateBack()
                } else {
                    val that = context as ComponentActivity
                    that.finish()
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left_outline),
                contentDescription = null
            )
        }
    },
    actions = actions,
    scrollBehavior = scrollBehavior
)