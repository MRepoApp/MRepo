package com.sanmer.mrepo.ui.screens.repository.viewmodule

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.ui.component.CollapsingTopAppBar
import com.sanmer.mrepo.ui.component.CollapsingTopAppBarDefaults
import com.sanmer.mrepo.ui.component.Logo
import com.sanmer.mrepo.ui.utils.LicenseContent
import com.sanmer.mrepo.ui.utils.expandedShape
import com.sanmer.mrepo.ui.utils.navigateBack

@Composable
fun ViewModuleTopBar(
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
    content = topBarContent(
        module = online
    ),
    navigationIcon = {
        IconButton(
            onClick = { navController.navigateBack() }
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
    module: OnlineModule
) : @Composable ColumnScope.() -> Unit = {
    val hasLicense = module.track.license.isNotBlank()

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Logo(
            iconRes = R.drawable.box_outline,
            modifier = Modifier.size(55.dp),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
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
                text = if (hasLicense) {
                    "ID = ${module.id}, License = ${module.track.license}"
                } else {
                    "ID = ${module.id}"
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
            LicenseItem(module.track.license)
        }

        val context = LocalContext.current
        TagItem(
            iconRes = R.drawable.tag_outline,
            onClick = {
                // TODO: Waiting for version 2.0 of util
                Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun TagItem(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) = FilledTonalIconButton(
    onClick = onClick,
    colors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f)
    ),
    modifier = Modifier.size(35.dp),
) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = null
    )
}

@Composable
private fun LicenseItem(
    licenseId: String
) = Box {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    TagItem(
        iconRes = R.drawable.receipt_search_outline,
        onClick = { openBottomSheet = true }
    )

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            shape = BottomSheetDefaults.expandedShape(15.dp),
            windowInsets = WindowInsets.navigationBars
        ) {
            Text(
                text = stringResource(id = R.string.license_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LicenseContent(licenseId = licenseId)
        }
    }
}