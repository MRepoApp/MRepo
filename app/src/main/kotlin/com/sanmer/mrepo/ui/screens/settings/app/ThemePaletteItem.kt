package com.sanmer.mrepo.ui.screens.settings.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.ui.theme.Colors
import com.sanmer.mrepo.ui.theme.getColor
import com.sanmer.mrepo.ui.theme.getColors

@Composable
fun ThemePaletteItem(
    themeColor: Int,
    isDarkMode: Boolean,
    onChange: (Int) -> Unit
) = LazyRow(
    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(15.dp)
) {
    if (Const.atLeastS) {
        item(Colors.Dynamic.id) {
            ThemeColorItem(
                id = Colors.Dynamic.id,
                themeColor = themeColor,
                isDarkMode = isDarkMode
            ) {
                onChange(it)
            }
        }
    }
    items(
        items = getColors(),
        key = { it }
    ) {
        ThemeColorItem(
            id = it,
            themeColor = themeColor,
            isDarkMode = isDarkMode
        ) { value ->
            onChange(value)
        }
    }
}

@Composable
private fun ThemeColorItem(
    id: Int,
    themeColor: Int,
    isDarkMode: Boolean,
    onClick: (Int) -> Unit
) {
    val color = getColor(id)
    val colorScheme = if (isDarkMode) color.darkColorScheme else color.lightColorScheme
    val selected = id == themeColor

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                onClick = { onClick(id) },
            )
            .background(
                color = colorScheme.surfaceVariant.copy(0.45f)
            )
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
                modifier = Modifier
                    .fillMaxSize()
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
                    painter = painterResource(id = R.drawable.tick_circle_bold),
                    contentDescription = null,
                    tint = colorScheme.primary
                )
            }
        }
    }
}