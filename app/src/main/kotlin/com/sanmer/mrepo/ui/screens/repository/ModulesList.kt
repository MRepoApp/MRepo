package com.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.ui.component.Logo
import com.sanmer.mrepo.ui.navigation.graph.createViewRoute
import com.sanmer.mrepo.ui.utils.navigatePopUpTo

@Composable
fun ModulesList(
    list: List<OnlineModule>,
    state: LazyListState,
    navController: NavController
) = LazyColumn(
    state = state,
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(5.dp),
) {
    items(
        items = list,
        key = { it.id }
    ) { module ->
        ModuleItem(module) {
            navController.navigatePopUpTo(createViewRoute(module))
        }
    }
}

@Composable
private fun ModuleItem(
    module: OnlineModule,
    onClick: () -> Unit
) = Surface(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick,
    shape = RoundedCornerShape(10.dp)
) {
    Row(
        modifier = Modifier.padding(all = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo(
            iconRes = R.drawable.box_outline,
            modifier = Modifier.size(35.dp),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                text = module.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = module.author,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = module.versionDisplay,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}