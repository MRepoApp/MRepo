package com.sanmer.mrepo.ui.screens.repository.viewmodule

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.ui.component.CollapsedTopAppBar
import com.sanmer.mrepo.ui.component.CollapsedTopAppBarDefaults
import com.sanmer.mrepo.ui.component.Logo
import com.sanmer.mrepo.ui.utils.LicenseContent
import com.sanmer.mrepo.ui.utils.navigateBack
import com.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun ViewModuleTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
    viewModel: ModuleViewModel = hiltViewModel()
) = CollapsedTopAppBar(
    title = {
        Text(
            text = viewModel.online.name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    content = topBarContent(
        module = viewModel.online
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
    colors = CollapsedTopAppBarDefaults.topAppBarColors(
        scrolledContainerColor = MaterialTheme.colorScheme.surface
    )
)

@Composable
private fun topBarContent(
    module: OnlineModule
) : @Composable ColumnScope.() -> Unit = {
    val hasLicense = module.license.isNotBlank()

    Row(
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
                .padding(start = 15.dp)
        ) {
            Text(
                text = module.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.horizontalScroll(rememberScrollState())
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = module.author,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.horizontalScroll(rememberScrollState())
            )
            Text(
                text = if (hasLicense) {
                    "ID = ${module.id}, License = ${module.license}"
                } else {
                    "ID = ${module.id}"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Row(
        modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (hasLicense) {
            LicenseItem(module.license)
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
        containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)
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
            shape = RoundedCornerShape(15.dp),
            scrimColor = Color.Transparent // TODO: Wait for the windowInsets parameter to be set
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