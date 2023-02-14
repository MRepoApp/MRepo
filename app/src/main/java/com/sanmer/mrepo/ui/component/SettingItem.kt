package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

@Composable
fun NormalItemForSetting(
    text: String,
    subText: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
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
                .padding(all = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconRes?.let {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = LocalContentColor.current
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
fun TitleItemForSetting(
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

@Composable
fun EditItemForSetting(
    @DrawableRes iconRes: Int? = null,
    title: String,
    text: String,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onChange: (String) -> Unit,
) {
    var edit by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(text) }
    if (edit) {
        EditDialog(
            title = title,
            text = text,
            onClose = { edit = false },
            onConfirm = {
                value = it
                if (it != text) {
                    onChange(value)
                }
            },
            supportingText = supportingText
        )
    }

    NormalItemForSetting(
        enabled = enabled,
        iconRes = iconRes,
        text = title,
        subText = text
    ) {
        edit = true
    }
}

@Composable
private fun EditDialog(
    onClose: () -> Unit,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit = {},
    title: String,
    text: String,
    supportingText: @Composable (() -> Unit)? = null
) {
    var value by remember { mutableStateOf(text) }

    AlertDialog(
        shape = RoundedCornerShape(25.dp),
        onDismissRequest = onClose,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                modifier = Modifier,
                textStyle = MaterialTheme.typography.bodyLarge,
                value = value,
                onValueChange = { value = it },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                supportingText = supportingText
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(value)
                    onClose()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_ok)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                    onClose()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel)
                )
            }
        }
    )
}

@Composable
fun MenuItemForSetting(
    @DrawableRes iconRes: Int? = null,
    itemList: List<String>,
    title: String,
    selected: Int,
    enabled: Boolean = true,
    onChange: (Int, String) -> Unit
) = MenuItemForSetting(
    iconRes = iconRes,
    itemList = itemList,
    title = title,
    selected = itemList[selected],
    enabled = enabled,
    onChange = onChange
)

@Composable
fun MenuItemForSetting(
    @DrawableRes iconRes: Int? = null,
    itemList: List<String>,
    title: String,
    selected: String,
    enabled: Boolean = true,
    onChange: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        offset = DpOffset(16.dp, 16.dp),
        shape = RoundedCornerShape(15.dp),
        onDismissRequest = { expanded = false },
        contentAlignment = Alignment.TopStart,
        surface = {
            NormalItemForSetting(
                enabled = enabled,
                iconRes = iconRes,
                text = title,
                subText = selected,
            ) {
                expanded = true
            }
        }
    ) {
        itemList.forEachIndexed { index, value ->
            MenuItem(
                value = value,
                selected = selected
            ) {
                expanded = false
                if (value != selected) {
                    onChange(index, value)
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
