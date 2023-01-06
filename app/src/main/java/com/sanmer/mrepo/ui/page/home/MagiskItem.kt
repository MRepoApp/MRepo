package com.sanmer.mrepo.ui.page.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.ui.component.StatusCard
import com.sanmer.mrepo.utils.MagiskUtils

@Composable
fun MagiskItem() {
    val context = LocalContext.current

    var access = stringResource(id = R.string.root_access,
        stringResource(id = R.string.root_status_none)
    )
    var provider = stringResource(id = R.string.root_provider,
        stringResource(id = R.string.root_status_not_available)
    )

    if (Status.Env.isSucceeded) {
        access = stringResource(id = R.string.root_access,
            stringResource(id = R.string.root_status_granted)
        )
        provider = stringResource(id = R.string.root_provider,
            Const.MAGISK_VERSION)
    }

    StatusCard(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(id = R.drawable.ic_magisk),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
        },
        title = stringResource(id = R.string.root_status),
        body1 = access,
        body2 = provider,
    ) {
        MagiskUtils.launchManager(context)
    }
}