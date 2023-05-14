package com.sanmer.mrepo.ui.screens.modules

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.DropdownMenu
import kotlinx.coroutines.launch

@Composable
fun MenuItem(
    expanded: Boolean,
    listState: LazyListState,
    onClose: () -> Unit
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
    offset = DpOffset(0.dp, 5.dp),
    shape = RoundedCornerShape(15.dp)
) {
    val scope = rememberCoroutineScope()

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.menu_scroll_top)) },
        onClick = {
            scope.launch {
                listState.scrollToItem(0)
            }
            onClose()
        }
    )

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.menu_scroll_bottom)) },
        onClick = {
            scope.launch {
                val count = listState.layoutInfo.totalItemsCount
                listState.scrollToItem(count)
            }
            onClose()
        }
    )
}