package com.sanmer.mrepo.ui.activity.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Config

@Composable
fun SetupScreen() {
    val context = LocalContext.current
    val that = context as SetupActivity

    Column(
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

        Spacer(modifier = Modifier.height(40.dp))

        ModeItem(
            title = stringResource(id = R.string.setup_root_title),
            text = stringResource(id = R.string.setup_root_desc)
        ) {
            Config.workingMode = Config.MODE_ROOT
            that.finish()
        }
    }
}

@Composable
private fun ModeItem(
    title: String,
    text: String,
    onClick: () -> Unit
) = OutlinedCard(
    onClick = onClick,
    modifier = Modifier
        .fillMaxWidth(0.65f),
    colors = CardDefaults.cardColors(),
) {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(50.dp))
    }
}
