package com.sanmer.mrepo.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.theme.AppTheme

@Composable
fun EditItem(
    @DrawableRes iconRes: Int? = null,
    title: String,
    subtitle: String,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onChange: (String) -> Unit,
) {
    var show by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(subtitle) }
    if (show) {
        TextFieldItem(
            title = title,
            text = subtitle,
            onClose = { show = false },
            onConfirm = {
                value = it
                if (it != subtitle) {
                    onChange(value)
                }
            },
            supportingText = {
                if (supportingText != null) {
                    supportingText()
                } else {
                    Text(text = stringResource(id = R.string.dialog_text_field_desc))
                }
            }
        )
    }

    NormalItem(
        enabled = enabled,
        iconRes = iconRes,
        text = title,
        subText = subtitle
    ) {
        show = true
    }
}

@Composable
private fun TextFieldItem(
    onClose: () -> Unit,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit = {},
    title: String,
    text: String,
    supportingText: @Composable (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var value by remember { mutableStateOf(text) }

    AlertDialog(
        shape = RoundedCornerShape(15.dp),
        onDismissRequest = onClose,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
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
                    keyboardController?.hide()
                    onClose()
                    onConfirm(value)
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
                    keyboardController?.hide()
                    onClose()
                    onCancel()
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
fun PickerItem(
    @DrawableRes iconRes: Int? = null,
    itemList: List<String>,
    title: String,
    selected: Int,
    enabled: Boolean = true,
    onChange: (Int, String) -> Unit
) = PickerItem(
    iconRes = iconRes,
    itemList = itemList,
    title = title,
    selected = itemList[selected],
    enabled = enabled,
    onChange = onChange
)

@Composable
fun PickerItem(
    @DrawableRes iconRes: Int? = null,
    itemList: List<String>,
    title: String,
    selected: String,
    enabled: Boolean = true,
    onChange: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {

        NormalItem(
            enabled = enabled,
            iconRes = iconRes,
            text = title,
            subText = selected,
        ) {
            expanded = true
        }

        CustomShape {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.TopStart
            ) {
                DropdownMenu(
                    expanded = expanded,
                    offset = DpOffset(16.dp, 16.dp),
                    onDismissRequest = { expanded = false }
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
        }

    }
}

@Composable
private fun MenuItem(
    value: String,
    selected: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
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
}

@Composable
private fun CustomShape(content: @Composable () -> Unit) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp)),
        content = content
    )
}

@Composable
private fun PickerItem(
    onClose: () -> Unit,
    onConfirm: (Int, String) -> Unit,
    onCancel: () -> Unit = {},
    title: String,
    itemList: List<String>,
    selected: String,
) {
    var value by remember { mutableStateOf(selected) }
    AlertDialog(
        shape = RoundedCornerShape(15.dp),
        onDismissRequest = onClose,
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                itemList.forEach {
                    val isSelected = it == value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(0.45f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .selectable(
                                selected = isSelected,
                                onClick = { value = it }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,

                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClose()
                    onConfirm(itemList.indexOf(value),  value)
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
                    onClose()
                    onCancel()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel)
                )
            }
        }
    )
}

@Preview(
    name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Composable
private fun TextFieldPreview() {
    AppTheme {
        TextFieldItem(
            onClose = { },
            onConfirm = { },
            onCancel = { },
            title = stringResource(id = R.string.settings_download_path),
            text = "/storage/emulated/0/Download"
        )
    }
}
