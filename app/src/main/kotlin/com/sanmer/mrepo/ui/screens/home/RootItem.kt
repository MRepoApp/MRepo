package com.sanmer.mrepo.ui.screens.home

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.viewmodel.HomeViewModel

@Composable
fun RootItem(
    viewModel: HomeViewModel = hiltViewModel()
) = Surface(
    shape = RoundedCornerShape(20.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
) {
    val suEvent by viewModel.suState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier
                .size(28.dp),
            painter = painterResource(id =
            if (suEvent.isSucceeded) {
                R.drawable.tick_circle_outline
            } else {
                R.drawable.slash_outline
            }),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (suEvent.isSucceeded) {
                    stringResource(id = R.string.root_status_access,
                        stringResource(id = R.string.root_status_granted)
                    )
                } else {
                    stringResource(id = R.string.root_status_access,
                        stringResource(id = R.string.root_status_none)
                    )
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (suEvent.isSucceeded) {
                    stringResource(id = R.string.root_status_provider,
                        viewModel.apiVersion)
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