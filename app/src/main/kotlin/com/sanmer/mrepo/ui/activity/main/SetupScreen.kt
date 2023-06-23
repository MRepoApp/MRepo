package com.sanmer.mrepo.ui.activity.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.screens.settings.workingmode.WorkingModeItem

@Composable
fun SetupScreen(
    setMode: (WorkingMode) -> Unit
) = Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = stringResource(id = R.string.setup_mode).toUpperCase(Locale.current),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(30.dp))
    WorkingModeItem(
        title = stringResource(id = R.string.setup_root_title),
        desc1 = stringResource(id = R.string.setup_root_desc1),
        desc2 = stringResource(id = R.string.setup_root_desc2),
        onClick = { setMode(WorkingMode.MODE_ROOT) }
    )

    Spacer(modifier = Modifier.height(20.dp))
    WorkingModeItem(
        title = stringResource(id = R.string.setup_non_root_title),
        desc1 = stringResource(id = R.string.setup_non_root_desc1),
        desc2 = stringResource(id = R.string.setup_non_root_desc2),
        onClick = { setMode(WorkingMode.MODE_NON_ROOT) }
    )
}