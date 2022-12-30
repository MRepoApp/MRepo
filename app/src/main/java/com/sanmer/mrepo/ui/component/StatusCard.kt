package com.sanmer.mrepo.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.theme.AppTheme

@Composable
fun StatusCard(
    title: String,
    body1: String,
    body2: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        onClick = onClick,
        colors = colors
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            if (leadingIcon != null) {
                leadingIcon()
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = body1,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = body2,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(
    name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Composable
private fun CardItemPreview() {
    AppTheme {
        StatusCard(
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(28.dp),
                    painter = painterResource(id = R.drawable.ic_verify_outline,),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
            },
            title = stringResource(id = R.string.status_magisk_modules_repo),
            body1 = stringResource(id = R.string.status_totals, "9"),
            body2 = stringResource(id = R.string.status_timestamp, "08-16 16:32:13.594292")
        )
    }
}