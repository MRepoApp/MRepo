package com.sanmer.mrepo.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
