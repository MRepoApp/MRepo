package com.sanmer.mrepo.ui.screens.apptheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.viewmodel.HomeViewModel

private sealed class DarkModeItem(
    val value: DarkMode,
    val icon: Int,
    val name: Int
) {
    object Auto : DarkModeItem(
        value = DarkMode.FOLLOW_SYSTEM,
        icon = R.drawable.auto_brightness_outline,
        name = R.string.app_theme_dark_theme_auto
    )
    object Light : DarkModeItem(
        value = DarkMode.ALWAYS_OFF,
        icon = R.drawable.sun_outline,
        name = R.string.app_theme_dark_theme_light
    )
    object Dark : DarkModeItem(
        value = DarkMode.ALWAYS_ON,
        icon = R.drawable.moon_outline,
        name = R.string.app_theme_dark_theme_dark
    )
}

private val modes = listOf(
    DarkModeItem.Auto,
    DarkModeItem.Light,
    DarkModeItem.Dark
)

@Composable
fun DarkModeItem(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())

    LazyRow(
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(
            items = modes,
            key = { it.value }
        ) {
            DarkModeItem(
                userData = userData,
                item = it
            ) { value ->
                viewModel.setDarkTheme(value)
            }
        }
    }
}

@Composable
private fun DarkModeItem(
    userData: UserData,
    item: DarkModeItem,
    onClick: (DarkMode) -> Unit
) {
    val selected = item.value == userData.darkMode

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                onClick = { onClick(item.value) }
            )
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(0.45f)
                } else {
                    MaterialTheme.colorScheme.outline.copy(0.1f)
                }
            ),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier
                .padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp),
                painter = painterResource(id = item.icon),
                contentDescription = null,
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    LocalContentColor.current
                }
            )
            Text(
                text = stringResource(id = item.name),
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Unspecified
                }
            )
        }
    }
}