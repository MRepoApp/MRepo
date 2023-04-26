package com.sanmer.mrepo.ui.activity.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

@Composable
fun SetupScreen(
    onRoot: () -> Unit,
    onNonRoot: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = stringResource(id = R.string.setup_mode).toUpperCase(Locale.current),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(30.dp))

    ModeItem(
        title = stringResource(id = R.string.setup_root_title),
        desc1 = stringResource(id = R.string.setup_root_desc1),
        desc2 = stringResource(id = R.string.setup_root_desc2),
        onClick = onRoot
    )

    Spacer(modifier = Modifier.height(20.dp))

    ModeItem(
        title = stringResource(id = R.string.setup_non_root_title),
        desc1 = stringResource(id = R.string.setup_non_root_desc1),
        desc2 = stringResource(id = R.string.setup_non_root_desc2),
        onClick = onNonRoot
    )
}

@Composable
private fun ModeItem(
    title: String,
    desc1: String,
    desc2: String,
    onClick: () -> Unit
) = OutlinedCard(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(0.65f),
) {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = desc1,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = desc2,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}