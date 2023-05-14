package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun SettingNormalItem(
    text: String,
    subText: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) = Row(
    modifier = modifier
        .alpha(alpha = if (enabled) 1f else 0.5f )
        .clickable(
            enabled = enabled,
            onClick = onClick
        )
        .padding(vertical = 16.dp,  horizontal = 25.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
) {
    iconRes?.let {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = LocalContentColor.current
        )

        Spacer(modifier = Modifier.width(25.dp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
fun SettingSwitchItem(
    text: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
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
            .padding(vertical = 16.dp, horizontal = 25.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(25.dp))
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

@Composable
fun <T> SettingMenuItem(
    @DrawableRes iconRes: Int? = null,
    items: Map<T, String>,
    title: String,
    selected: T,
    enabled: Boolean = true,
    onChange: (T, String) -> Unit
) = SettingMenuItem(
    iconRes = iconRes,
    items = items,
    title = title,
    selected = items[selected] ?: items.values.first(),
    enabled = enabled,
    onChange = onChange
)

@Composable
fun <T> SettingMenuItem(
    @DrawableRes iconRes: Int? = null,
    items: Map<T, String>,
    title: String,
    selected: String,
    enabled: Boolean = true,
    onChange: (T, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        offset = DpOffset(16.dp, 16.dp),
        shape = RoundedCornerShape(15.dp),
        onDismissRequest = { expanded = false },
        contentAlignment = Alignment.TopStart,
        surface = {
            SettingNormalItem(
                enabled = enabled,
                iconRes = iconRes,
                text = title,
                subText = selected,
            ) {
                expanded = true
            }
        }
    ) {
        items.forEach { (key, value) ->
            MenuItem(
                value = value,
                selected = selected
            ) {
                expanded = false
                if (value != selected) {
                    onChange(key, value)
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    value: String,
    selected: String,
    onClick: () -> Unit
) = DropdownMenuItem(
    modifier = Modifier
        .background(
            if (value == selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Unspecified
            }
        ),
    text = { Text(text = value) },
    onClick = onClick
)
