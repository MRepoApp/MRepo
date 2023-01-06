package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SwitchItem(
    text: String,
    subText: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    colorful: Boolean = false,
    enabled: Boolean = true,
    checked: Boolean = false,
    onChange: (Boolean) -> Unit,
) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(
        modifier = modifier
            .alpha(alpha = if (enabled) 1f else 0.5f )
            .selectable(
                enabled = enabled,
                selected = isChecked,
                onClick = {
                    isChecked = !isChecked
                    onChange(isChecked)
                },
                role = Role.Switch
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
                    tint = if (colorful) Color.Unspecified else LocalContentColor.current
                )

                Spacer(modifier = Modifier.width(18.dp))
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Switch(
                    checked = isChecked,
                    onCheckedChange = null
                )

            }
        }
    }
}
