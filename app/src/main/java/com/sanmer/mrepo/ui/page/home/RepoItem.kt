package com.sanmer.mrepo.ui.page.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.app.status.Event
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.ui.component.StatusCard
import com.sanmer.mrepo.ui.expansion.navigatePopUpTo
import com.sanmer.mrepo.ui.modify.CircularProgressIndicator
import com.sanmer.mrepo.ui.navigation.MainGraph
import com.sanmer.mrepo.utils.module.ModuleLoader

@Composable
fun RepoItem(
    navController: NavController
) {
    var title = stringResource(id = R.string.status_loading_repo)
    var iconRes = R.drawable.information_outline
    var size = stringResource(id = R.string.root_status_none)
    var timestamp = stringResource(id = R.string.root_status_not_available)

    when (Status.Online.event) {
        Event.SUCCEEDED -> {
            title = stringResource(id = R.string.status_magisk_modules_repo)
            iconRes = R.drawable.verify_outline
            size = Constant.online.size.toString()
            timestamp = Status.Online.timestamp
        }
        Event.FAILED -> {
            title = stringResource(id = R.string.status_load_failed)
        }
        else -> {}
    }

    StatusCard(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxWidth(),
        leadingIcon = {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                if (Status.Online.isFinished) {
                    Icon(
                        modifier = Modifier
                            .size(28.dp),
                        painter = painterResource(id = iconRes),
                        contentDescription = null
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        },
        title = title,
        body1 = stringResource(id = R.string.status_totals, size),
        body2 = stringResource(id = R.string.status_timestamp, timestamp),
    ) {
        if (Status.Online.isSucceeded) {
            navController.navigatePopUpTo(MainGraph.Modules.route)
        }
        if (Status.Online.isFailed) {
            ModuleLoader.getRepo()
        }
    }
}
