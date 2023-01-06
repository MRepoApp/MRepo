package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun NormalItem(
    text: String,
    subText: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    colorfulIcon: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .alpha(alpha = if (enabled) 1f else 0.5f )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconRes != null) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = if (colorfulIcon) Color.Unspecified else LocalContentColor.current
                )

                Spacer(modifier = Modifier.width(18.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun NormalTitle(
    text: String
) {
    Spacer(modifier = Modifier.height(18.dp))
    Row {
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}