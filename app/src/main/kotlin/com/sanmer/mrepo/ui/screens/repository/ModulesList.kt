package com.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.ui.component.FastScrollbar
import com.sanmer.mrepo.ui.component.Logo
import com.sanmer.mrepo.ui.navigation.graphs.createViewRoute
import com.sanmer.mrepo.ui.utils.navigatePopUpTo
import com.sanmer.mrepo.ui.utils.rememberFastScroller
import com.sanmer.mrepo.ui.utils.scrollbarState
import com.sanmer.mrepo.viewmodel.RepositoryViewModel

@Composable
fun ModulesList(
    list: List<OnlineModule>,
    state: LazyListState,
    navController: NavController,
    getModuleState: @Composable (OnlineModule) -> RepositoryViewModel.ModuleState
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(
            items = list,
            key = { it.id }
        ) { module ->
            ModuleItem(
                module = module,
                onClick = { navController.navigatePopUpTo(createViewRoute(module)) },
                getModuleState = getModuleState
            )
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

@Composable
private fun ModuleItem(
    module: OnlineModule,
    onClick: () -> Unit,
    getModuleState: @Composable (OnlineModule) -> RepositoryViewModel.ModuleState
) = Surface(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick,
    shape = RoundedCornerShape(10.dp)
) {
    val moduleState = getModuleState(module)

    Row(
        modifier = Modifier.padding(all = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo(
            iconRes = R.drawable.box_outline,
            modifier = Modifier.size(40.dp),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Column(
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = module.name,
                style = MaterialTheme.typography.titleSmall
                    .copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = module.author,
                style = MaterialTheme.typography.bodyMedium.copy(),
            )
            Text(
                text = module.versionDisplay,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            if (moduleState.hasLabel) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (moduleState.hasLicense) {
                        LabelItem(text = module.license)
                    }

                    if (moduleState.installed) {
                        LabelItem(text = stringResource(id = R.string.module_label_installed))
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelItem(
    text: String
) = Box(
    modifier = Modifier.background(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(3.dp)
    )
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall
            .copy(fontSize = 8.sp),
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .padding(horizontal = 3.dp, vertical = 1.dp)
            .align(Alignment.Center)
    )
}