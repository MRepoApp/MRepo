package dev.sanmer.mrepo.ui.screens.repository.view.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.utils.extensions.openUrl

@Composable
internal fun AboutPage(
    online: OnlineModule
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    ValueItem(
        key = stringResource(id = R.string.view_module_homepage),
        value = online.metadata.homepage,
        icon = R.drawable.home
    )

    ValueItem(
        key = stringResource(id = R.string.view_module_source),
        value = online.metadata.source,
        icon = R.drawable.brand_git
    )

    ValueItem(
        key = stringResource(id = R.string.view_module_support),
        value = online.metadata.support,
        icon = R.drawable.heart_handshake
    )
}

@Composable
private fun ValueItem(
    key: String,
    value: String,
    @DrawableRes icon: Int
) {
    if (value.isBlank()) return
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .clickable { context.openUrl(value) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        ElevatedAssistChip(
            onClick = { context.openUrl(value) },
            label = { Text(text = key) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
    }

    HorizontalDivider(thickness = 0.9.dp)
}