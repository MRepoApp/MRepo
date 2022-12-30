package com.sanmer.mrepo.ui.activity.install

import androidx.activity.ComponentActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.utils.InstallUtils
import com.sanmer.mrepo.app.status.Event
import com.sanmer.mrepo.ui.utils.NavigateUpToolBar
import com.sanmer.mrepo.utils.SvcPower

@Composable
fun InstallScreen(
    utils: InstallUtils = InstallUtils,
    list: SnapshotStateList<String>
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { NavigateUpToolBar(
            title = R.string.install_title,
            subtitle = when(utils.event) {
                Event.LOADING -> R.string.install_flashing
                Event.FAILED -> R.string.install_failure
                else -> R.string.install_done
            },
            onClick = {
                if (utils.isFinish) {
                    val that = context as ComponentActivity
                    that.finish()
                }
            },
            scrollBehavior = scrollBehavior
        ) },
        floatingActionButton = {
            if (utils.isSuccess) {
                RebootButton()
            }
        },
    ) {
        ConsoleList(
            list = list,
            paddingValues = it
        )
    }
}

@Composable
private fun RebootButton() {
    ExtendedFloatingActionButton(
        onClick = {
            SvcPower.reboot()
        },
        text = {
            Text(text = stringResource(id = R.string.install_reboot),)
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh_outline),
                contentDescription = null
            )
        }
    )
}

@Composable
private fun ConsoleList(
    modifier: Modifier = Modifier,
    list: SnapshotStateList<String>,
    paddingValues: PaddingValues
) {
    val state = rememberLazyListState()

    LazyColumn(
        state = state,
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(paddingValues = paddingValues)
            .fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 4.dp),
    ) {
        items(list) {
            ConsoleItem(text = it)
        }
    }
}

@Composable
private fun ConsoleItem(
    text: String
) {
    Row {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
