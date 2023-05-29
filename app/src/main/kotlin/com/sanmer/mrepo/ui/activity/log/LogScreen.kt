package com.sanmer.mrepo.ui.activity.log

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.service.LogcatService
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.ui.component.FastScrollbar
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.utils.rememberFastScroller
import com.sanmer.mrepo.ui.utils.scrollbarState
import com.sanmer.mrepo.utils.log.LogText
import com.sanmer.mrepo.utils.log.Logcat
import com.sanmer.mrepo.utils.log.Logcat.toTextPriority

private val priorities = listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR")
object LogColors {
    @Composable
    fun priorityContainer(priority: Int) = when (priority) {
        Log.VERBOSE -> Color(0xFFD6D6D6)
        Log.DEBUG -> Color(0xFF305D78)
        Log.INFO -> Color(0xFF6A8759)
        Log.WARN -> Color(0xFFBBB529)
        Log.ERROR -> Color(0xFFCF5B56)
        Log.ASSERT -> Color(0xFF8B3C3C)
        else -> MaterialTheme.colorScheme.primary
    }

    @Composable
    fun priorityContent(priority: Int) = when (priority) {
        Log.VERBOSE -> Color(0xFF000000)
        Log.DEBUG -> Color(0xFFBBBBBB)
        Log.INFO -> Color(0xFFE9F5E6)
        Log.WARN -> Color(0xFF000000)
        Log.ERROR -> Color(0xFF000000)
        Log.ASSERT -> Color(0xFFFFFFFF)
        else -> MaterialTheme.colorScheme.onPrimary
    }

    @Composable
    fun message(priority: Int) = when (priority) {
        Log.VERBOSE -> Color(0xFFBBBBBB)
        Log.DEBUG -> Color(0xFF299999)
        Log.INFO -> Color(0xFFABC023)
        Log.WARN -> Color(0xFFBBB529)
        Log.ERROR -> Color(0xFFFF6B68)
        Log.ASSERT -> Color(0xFFFF6B68)
        else -> LocalContentColor.current
    }
}

@Composable
fun LogScreen() {
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var priority by remember { mutableStateOf("DEBUG") }

    val console by remember {
        derivedStateOf {
            LogcatService.console.filter {
                it.priority >= priorities.indexOf(priority) + 2
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LogTopBar(
                scrollBehavior = scrollBehavior,
                priority = priority,
                onPriority = { priority = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = state
            ) {
                items(console) { value ->
                    Column(
                        modifier = Modifier.padding(horizontal = 1.dp)
                    ) {
                        LogItem(value)
                        Divider()
                    }
                }
            }

            FastScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
                state = state.scrollbarState(),
                orientation = Orientation.Vertical,
                scrollInProgress = state.isScrollInProgress,
                onThumbDisplaced = state.rememberFastScroller(),
            )
        }
    }
}

@Composable
private fun LogTopBar(
    priority: String,
    onPriority: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) = NavigateUpTopBar(
    title = R.string.page_log_viewer,
    actions = {
        val context = LocalContext.current
        IconButton(
            onClick = { Logcat.shareLogs(context) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_outline),
                contentDescription = null
            )
        }

        var prioritySelect by remember { mutableStateOf(false) }
        IconButton(
            onClick = { prioritySelect = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sort_outline),
                contentDescription = null
            )

            PrioritySelect(
                expanded = prioritySelect,
                selected = priority,
                onClose = { prioritySelect = false },
                onClick = onPriority
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun LogItem(
    value: LogText
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Max),
    verticalAlignment = Alignment.CenterVertically
) {
    Box(
        modifier = Modifier
            .background(
                color = LogColors.priorityContainer(value.priority)
            )
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.priority.toTextPriority(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = LogColors.priorityContent(value.priority)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value.tag,
            style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value.message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = LogColors.message(value.priority)
        )

        Text(
            text = "${value.time} ${value.process}",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Serif
            ),
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun PrioritySelect(
    expanded: Boolean,
    selected: String,
    onClose: () -> Unit,
    onClick: (String) -> Unit,
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
    offset = DpOffset(0.dp, 5.dp),
    shape = RoundedCornerShape(15.dp)
) {
    priorities.forEach {
        DropdownMenuItem(
            modifier = Modifier
                .background(
                    if (it == selected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        Color.Unspecified
                    }
                ),
            text = { Text(text = it) },
            onClick = {
                if (it != selected) onClick(it)
                onClose()
            }
        )
    }
}
