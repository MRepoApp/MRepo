package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SwitchItem(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    subText: String? = null,
    @DrawableRes iconRes: Int? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .alpha(alpha = if (enabled) 1f else 0.5f )
            .toggleable(
                value = checked,
                enabled = enabled,
                onValueChange = onChange,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = rememberRipple()
            )
            .padding(all = 18.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(18.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
            )
            subText?.let {
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = null
        )

    }
}
