package dev.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.mrepo.ui.component.scrollbar.VerticalFastScrollbar
import dev.sanmer.mrepo.ui.utils.navigateSingleTopTo
import dev.sanmer.mrepo.viewmodel.ModuleViewModel
import dev.sanmer.mrepo.viewmodel.RepositoryViewModel

@Composable
internal fun ModulesList(
    state: LazyListState,
    navController: NavController,
    list: List<RepositoryViewModel.ModuleWrapper>,
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(
            items = list,
            key = { it.original.id }
        ) { module ->
            ModuleItem(
                module = module,
                onClick = {
                    navController.navigateSingleTopTo(
                        ModuleViewModel.putModuleId(module.original)
                    )
                }
            )
        }
    }

    VerticalFastScrollbar(
        state = state,
        modifier = Modifier.align(Alignment.CenterEnd)
    )
}