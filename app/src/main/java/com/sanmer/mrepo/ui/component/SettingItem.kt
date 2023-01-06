package com.sanmer.mrepo.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

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
        TextFieldDialog(
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
                    Text(text = stringResource(id = R.string.dialog_empty_default))
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
private fun TextFieldDialog(
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
