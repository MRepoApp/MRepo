package com.sanmer.mrepo.ui.activity.log

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.service.LogcatService
import com.sanmer.mrepo.ui.utils.NavigateUpTopBar
import com.sanmer.mrepo.utils.log.LogItem

private val priorities = listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR")

@Composable
fun LogScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var priority by remember { mutableStateOf("DEBUG") }

    val console by remember {
        derivedStateOf {
            LogcatService.console.asReversed()
                .filter { it.priority >= priorities.indexOf(priority) + 2 }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LogTopBar(
                scrollBehavior = scrollBehavior,
                priority = priority
            ) {
                priority = it
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it),
            contentPadding = PaddingValues(horizontal = 1.dp)
        ) {
            items(console) { value ->
                Column {
                    LogItem(value)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun LogTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    priority: String,
    onClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    NavigateUpTopBar(
        title = R.string.settings_log_viewer,
        actions = {
            IconButton(
                onClick = { expanded = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sort_outline),
                    contentDescription = null
                )
            }

            PrioritySelect(
                expanded = expanded,
                selected = priority,
                onClose = { expanded = false },
                onClick = onClick
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun LogItem(
    value: LogItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = when (value.priority) {
                        Log.VERBOSE -> Color(0xFFD6D6D6)
                        Log.DEBUG -> Color(0xFF6A8759)
                        Log.INFO -> Color(0xFF305D78)
                        Log.WARN -> Color(0xFFBBB529)
                        Log.ERROR -> Color(0xFFCF5B56)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                .fillMaxHeight()
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when(value.priority) {
                    Log.VERBOSE -> "V"
                    Log.DEBUG -> "D"
                    Log.INFO -> "I"
                    Log.WARN -> "W"
                    Log.ERROR -> "E"
                    else -> "N"
                },
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = when(value.priority) {
                    Log.VERBOSE -> Color(0xFF000000)
                    Log.DEBUG -> Color(0xFFE9F5E6)
                    Log.INFO -> Color(0xFFBBBBBB)
                    Log.WARN -> Color(0xFF000000)
                    Log.ERROR -> Color(0xFF000000)
                    else -> MaterialTheme.colorScheme.onPrimary
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value.tag,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value.message,
                style = MaterialTheme.typography.bodyMedium,
                color = when(value.priority) {
                    Log.WARN -> Color(0xFFBBB529)
                    Log.ERROR -> Color(0xFFCF5B56)
                    else -> Color.Unspecified
                }
            )

            Text(
                text = "${value.time} ${value.process}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun PrioritySelect(
    expanded: Boolean,
    selected: String,
    onClose: () -> Unit,
    onClick: (String) -> Unit,
) {
    CustomShape {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onClose,
            offset = DpOffset(15.dp, 0.dp)
        ) {
            priorities.forEach {
                MenuItem(
                    value = it,
                    selected = selected
                ) {
                    if (it != selected) onClick(it)
                    onClose()
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

@Composable
private fun CustomShape(
    content: @Composable () -> Unit
) = MaterialTheme(
    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp)),
    content = content
)
