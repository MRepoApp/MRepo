package com.sanmer.mrepo.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.provider.EnvProvider

@Composable
fun RootItem() = Surface(
    shape = RoundedCornerShape(20.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    onClick = {
        if (Status.Env.isFailed) {
            EnvProvider.init()
        }
    }
) {
    Row(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = if (EnvProvider.isRoot) 14.dp else 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (EnvProvider.isRoot) {
            Icon(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(id = R.drawable.ic_magisk),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (Status.Env.isSucceeded) {
                    stringResource(id = R.string.root_status_access,
                        stringResource(id = R.string.root_status_granted)
                    )
                } else {
                    stringResource(id = R.string.root_status_access,
                        stringResource(id = R.string.root_status_none)
                    )
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (Status.Env.isSucceeded) {
                    stringResource(id = R.string.root_status_provider,
                        EnvProvider.version)
                } else {
                    stringResource(id = R.string.root_status_provider,
                        stringResource(id = R.string.root_status_not_available)
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}