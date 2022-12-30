package com.sanmer.mrepo.ui.component

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.theme.AppTheme

@Composable
fun EventCard(
    @StringRes stringRes: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 10.dp),
            text = stringResource(id = stringRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedCard(
            modifier = Modifier,
            shape = RoundedCornerShape(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
        }

    }
}

@Preview(
    name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Composable
private fun EventCardPreview() {
    AppTheme {
        EventCard(
            stringRes = R.string.device_info
        ) {
            EventCardItem(
                key = stringResource(id = R.string.device_name),
                value = "Xiaomi Mi MIX 2S"
            )

            EventCardItem(
                key = stringResource(id = R.string.device_system_version),
                value = "12 (API 32)"
            )

            EventCardItem(
                key = stringResource(id = R.string.device_system_abi),
                value = "arm64-v8a"
            )
        }
    }
}