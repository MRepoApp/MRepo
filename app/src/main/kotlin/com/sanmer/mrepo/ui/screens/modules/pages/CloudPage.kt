package com.sanmer.mrepo.ui.screens.modules.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.ui.component.NormalChip
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.navigation.graph.ModulesGraph.View.toRoute
import com.sanmer.mrepo.ui.screens.modules.Pages
import com.sanmer.mrepo.ui.utils.navigatePopUpTo
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun CloudPage(
    userData: UserData,
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel(),
) {
    val list = viewModel.onlineValue

    if (list.isEmpty()) {
        PageIndicator(
            icon = R.drawable.cloud_connection_outline,
            text = if (viewModel.isSearch) R.string.modules_page_search_empty else R.string.modules_page_cloud_empty
        )
    } else {
        ModulesList(
            list = list,
            userData = userData,
            navController = navController,
        )
    }
}

@Composable
private fun ModulesList(
    list: List<OnlineModule>,
    userData: UserData,
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val state = rememberLazyListState()
    SideEffect { viewModel.updateListSate(Pages.Cloud.id to state) }

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(
            items = list,
            key = { it.id }
        ) { module ->
            OnlineModuleItem(module) {
                navController.navigatePopUpTo(module.id.toRoute())
            }
        }
    }
}

@Composable
private fun OnlineModuleItem(
    module: OnlineModule,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(25.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(end = 4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(id = R.string.module_author, module.author),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.background
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NormalChip(
                    painter = painterResource(id = R.drawable.document_code_outline),
                    text = module.versionDisplay
                )

                if (module.license.isNotBlank()) {
                    NormalChip(
                        painter = painterResource(id = R.drawable.document_text_outline),
                        text = module.license
                    )
                }
            }
        }
    }
}