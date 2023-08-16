package com.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.ui.component.Logo

@Composable
fun ModuleItem(
    module: OnlineModule,
    updatable: Boolean,
    installed: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) = Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    enabled = enabled,
    shape = RoundedCornerShape(10.dp)
) {
    val hasLicense = module.track.license.isNotBlank()
    val hasLabel = installed or hasLicense

    Row(
        modifier = Modifier.padding(all = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (updatable) {
            Logo(
                icon = R.drawable.import_outline,
                modifier = Modifier.size(40.dp),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            Logo(
                icon = R.drawable.box_outline,
                modifier = Modifier.size(40.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = module.versionDisplay,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            if (hasLabel) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (hasLicense) {
                        LabelItem(
                            text = module.track.license.toUpperCase(Locale.current)
                        )
                    }

                    if (installed) {
                        LabelItem(
                            text = stringResource(id = R.string.module_label_installed).toUpperCase(Locale.current)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelItem(text: String) = Box(
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
            .padding(horizontal = 3.dp)
            .align(Alignment.Center)
    )
}