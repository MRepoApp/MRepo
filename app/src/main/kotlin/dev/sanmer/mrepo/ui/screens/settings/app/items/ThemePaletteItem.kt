package dev.sanmer.mrepo.ui.screens.settings.app.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.compat.BuildCompat
import dev.sanmer.mrepo.ui.theme.Colors

@Composable
internal fun ThemePaletteItem(
    themeColor: Int,
    isDarkMode: Boolean,
    onChange: (Int) -> Unit
) = FlowRow(
    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    if (BuildCompat.atLeastS) {
        ThemeColorItem(
            id = Colors.Dynamic.id,
            themeColor = themeColor,
            isDarkMode = isDarkMode,
            onChange = onChange
        )
    }

    Colors.colorIds.forEach {
        ThemeColorItem(
            id = it,
            themeColor = themeColor,
            isDarkMode = isDarkMode,
            onChange = onChange
        )
    }
}

@Composable
private fun ThemeColorItem(
    id: Int,
    themeColor: Int,
    isDarkMode: Boolean,
    onChange: (Int) -> Unit
) {
    val selected by remember(themeColor) {
        derivedStateOf { id == themeColor }
    }

    val color = Colors.getColor(id)
    val colorScheme by remember(isDarkMode) {
        derivedStateOf {
            when {
                isDarkMode -> color.darkColorScheme
                else -> color.lightColorScheme
            }
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                onClick = { onChange(id) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(color = colorScheme.surfaceColorAtElevation(6.dp))
            .size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxSize(0.75f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(color = colorScheme.primaryContainer)
                )

                Spacer(modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorScheme.tertiaryContainer)
                )
            }

            Spacer(modifier = Modifier
                .fillMaxSize(0.5f)
                .clip(CircleShape)
                .background(
                    color = if (selected) {
                        colorScheme.onPrimary
                    } else {
                        colorScheme.primary
                    }
                )
            )

            if (selected) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(id = R.drawable.circle_check_filled),
                    contentDescription = null,
                    tint = colorScheme.primary
                )
            }
        }
    }
}