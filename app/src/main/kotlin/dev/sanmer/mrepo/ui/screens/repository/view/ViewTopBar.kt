package dev.sanmer.mrepo.ui.screens.repository.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.ui.component.CollapsingTopAppBar
import dev.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import dev.sanmer.mrepo.ui.component.Logo
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.screens.repository.view.items.LicenseItem
import dev.sanmer.mrepo.ui.screens.repository.view.items.TagItem
import dev.sanmer.mrepo.utils.extensions.openUrl

@Composable
internal fun ViewTopBar(
    online: OnlineModule,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = CollapsingTopAppBar(
    title = {
        Text(
            text = online.name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    content = {
        TopBarContent(
            module = online
        )
    },
    navigationIcon = {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior,
    colors = CollapsingTopAppBarDefaults.topAppBarColors(
        scrolledContainerColor = MaterialTheme.colorScheme.surface
    )
)

@Composable
private fun TopBarContent(
    module: OnlineModule
) {
    val context = LocalContext.current
    val userPreferences = LocalUserPreferences.current
    val repositoryMenu = userPreferences.repositoryMenu

    val hasLicense by remember(module.metadata) {
        derivedStateOf { module.metadata.license.isNotBlank() }
    }
    val hasDonate by remember(module.metadata) {
        derivedStateOf { module.metadata.donate.isNotBlank() }
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (repositoryMenu.showIcon) {
            Logo(
                icon = R.drawable.box,
                modifier = Modifier.size(55.dp),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = module.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = module.author,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildAnnotatedString {
                    append("ID = ${module.id}")
                    if (hasLicense) {
                        append(", ")
                        append("License = ${module.metadata.license}")
                    }
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (hasLicense) {
            LicenseItem(
                licenseId = module.metadata.license
            )
        }

        if (hasDonate) {
            TagItem(
                icon = R.drawable.currency_dollar,
                onClick = { context.openUrl(module.metadata.donate) }
            )
        }
    }
}