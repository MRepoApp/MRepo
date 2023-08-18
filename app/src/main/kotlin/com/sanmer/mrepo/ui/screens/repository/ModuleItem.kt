package com.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.state.OnlineState
import com.sanmer.mrepo.ui.component.Logo
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.utils.extensions.toDate

@Composable
fun ModuleItem(
    module: OnlineModule,
    state: OnlineState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) = Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    enabled = enabled,
    shape = RoundedCornerShape(10.dp)
) {
    Row(
        modifier = Modifier.padding(all = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val userPreferences = LocalUserPreferences.current
        val menu = userPreferences.repositoryMenu
        val hasLabel = (state.hasLicense && menu.showLicense)
                || state.installed

        if (menu.showIcon) {
            Logo(
                icon = R.drawable.box_outline,
                modifier = Modifier.size(40.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = module.versionDisplay,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            if (menu.showUpdatedTime) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(
                        id = R.string.module_update_at,
                        state.lastUpdated.toDate()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (hasLabel) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (menu.showLicense) {
                        LabelItem(
                            text = module.track.license.toUpperCase(Locale.current)
                        )
                    }

                    when {
                        state.updatable ->
                            LabelItem(
                                text = stringResource(id = R.string.module_new)
                                    .toUpperCase(Locale.current),
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )

                        state.installed ->
                            LabelItem(
                                text = stringResource(id = R.string.module_installed)
                                    .toUpperCase(Locale.current)
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun LabelItem(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = RoundedCornerShape(3.dp)
) {
    if (text.isBlank()) return

    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
                .copy(fontSize = 8.sp),
            color = contentColor,
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .align(Alignment.Center)
        )
    }
}