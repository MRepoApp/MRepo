package com.sanmer.mrepo.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.utils.expansion.toLongOr
import com.sanmer.mrepo.works.Works
import java.util.concurrent.TimeUnit

sealed class MTimeUnit(
    @StringRes val label: Int,
    val unit: TimeUnit,
) {
    object Minute : MTimeUnit(
        label = R.string.time_unit_minute,
        unit = TimeUnit.MINUTES
    )
    object Hour : MTimeUnit(
        label = R.string.time_unit_hour,
        unit = TimeUnit.HOURS
    )
    object Day : MTimeUnit(
        label = R.string.time_unit_day,
        unit = TimeUnit.DAYS
    )
}

val mTimeUnits = listOf(
    MTimeUnit.Minute,
    MTimeUnit.Hour,
    MTimeUnit.Day
)

fun getMTimeUnits(unit: TimeUnit) = mTimeUnits.find { it.unit == unit } ?: MTimeUnit.Minute

@Composable
fun TasksPeriodDialog(
    onClose: () -> Unit,
) {
    var count by remember { mutableStateOf(Config.tasksPeriodCount.toString()) }
    var unit by remember { mutableStateOf(Config.tasksPeriodUnit) }

    AlertDialog(
        shape = RoundedCornerShape(25.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.settings_tasks_period)) },
        text = {
            OutlinedTextField(
                textStyle = MaterialTheme.typography.bodyLarge,
                value = count,
                onValueChange = { count = it },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                supportingText = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.settings_tasks_period_dialog_desc1),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = stringResource(id = R.string.settings_tasks_period_dialog_desc2))
                    }
                },
                trailingIcon = {
                    TimeUnitSelect(selected = unit) {
                        unit = it
                    }
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Config.tasksPeriodCount = count.toLongOr(12)
                    Config.tasksPeriodUnit = unit
                    Works.resetPeriodTasks()
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
                onClick = onClose
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel)
                )
            }
        }
    )
}

@Composable
private fun TimeUnitSelect(
    selected: TimeUnit,
    onClick: (TimeUnit) -> Unit
) {
    val value = getMTimeUnits(unit = selected)
    var expanded by remember { mutableStateOf(false) }
    val animateZ by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        shape = RoundedCornerShape(15.dp),
        contentAlignment = Alignment.BottomEnd,
        surface = {
            FilterChip(
                modifier = Modifier.padding(horizontal = 10.dp),
                selected = true,
                onClick = { expanded = true },
                label = { Text(text = stringResource(id = value.label)) },
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer {
                                rotationZ = animateZ
                            },
                        painter = painterResource(id = R.drawable.arrow_down_bold),
                        contentDescription = null
                    )
                }
            )
        }
    ) {
        mTimeUnits.forEach {
            MenuItem(
                value = it,
                selected = value
            ) {
                onClick(it.unit)
                expanded = false
            }
        }
    }
}

@Composable
private fun MenuItem(
    value: MTimeUnit,
    selected: MTimeUnit,
    onClick: () -> Unit
) = DropdownMenuItem(
    modifier = Modifier
        .background(
            if (value.unit == selected.unit) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Unspecified
            }
        ),
    text = {
        Text(text = stringResource(id = value.label)) },
    onClick = onClick
)