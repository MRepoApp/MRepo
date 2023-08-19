package com.sanmer.mrepo.ui.screens.repository.viewmodule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.TrackJson
import com.sanmer.mrepo.ui.component.CollapsingTopAppBar
import com.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import com.sanmer.mrepo.ui.component.Logo
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.screens.repository.viewmodule.items.LicenseItem
import com.sanmer.mrepo.ui.screens.repository.viewmodule.items.TagItem
import com.sanmer.mrepo.ui.screens.repository.viewmodule.items.TrackItem
import com.sanmer.mrepo.utils.extensions.openUrl

@Composable
fun ViewModuleTopBar(
    online: OnlineModule,
    tracks: List<Pair<Repo, TrackJson>>,
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
    content = topBarContent(
        module = online,
        tracks = tracks
    ),
    navigationIcon = {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left_outline),
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
private fun topBarContent(
    module: OnlineModule,
    tracks: List<Pair<Repo, TrackJson>>
) : @Composable ColumnScope.() -> Unit = {
    val userPreferences = LocalUserPreferences.current
    val repositoryMenu = userPreferences.repositoryMenu

    val context = LocalContext.current
    val hasLicense = module.track.license.isNotBlank()
    val hasDonate = module.track.donate.isNotBlank()

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (repositoryMenu.showIcon) {
            Logo(
                icon = R.drawable.box_outline,
                modifier = Modifier.size(55.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        append("License = ${module.track.license}")
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
        TrackItem(
            tracks = tracks
        )

        if (hasLicense) {
            LicenseItem(
                licenseId = module.track.license
            )
        }

        if (hasDonate) {
            TagItem(
                icon = R.drawable.coin_outline,
                onClick = { context.openUrl(module.track.donate) }
            )
        }
    }
}