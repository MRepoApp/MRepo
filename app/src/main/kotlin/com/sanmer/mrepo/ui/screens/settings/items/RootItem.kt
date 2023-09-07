package com.sanmer.mrepo.ui.screens.settings.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.sanmer.mrepo.app.event.Event
import com.sanmer.mrepo.app.event.isSucceeded

@Composable
fun RootItem(
    suState: Event,
    apiVersion: String
) = Surface(
    modifier = Modifier.padding(all = 18.dp),
    shape = RoundedCornerShape(15.dp),
    color = MaterialTheme.colorScheme.secondaryContainer
) {
    Row(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = if (suState.isSucceeded) {
                R.drawable.verify_bold
            } else {
                R.drawable.information_bold
            }),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (suState.isSucceeded) {
                    stringResource(id = R.string.settings_root_access,
                        stringResource(id = R.string.settings_root_granted))
                } else {
                    stringResource(id = R.string.settings_root_access,
                        stringResource(id = R.string.settings_root_none))
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = if (suState.isSucceeded) {
                    stringResource(id = R.string.settings_root_provider,
                        apiVersion)
                } else {
                    stringResource(id = R.string.settings_root_provider,
                        stringResource(id = R.string.settings_root_not_available))
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}