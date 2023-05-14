package com.sanmer.mrepo.ui.screens.repository.viewmodule

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.json.ModuleUpdateItem
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.ui.component.ExpandableItem
import com.sanmer.mrepo.ui.component.MarkdownText
import com.sanmer.mrepo.viewmodel.DetailViewModel

@Composable
fun ChangelogItem(
    viewModel: DetailViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var versionCode by remember { mutableStateOf(viewModel.module.versionCode) }

    LaunchedEffect(expanded, versionCode) {
        if (expanded) viewModel.getChangelog(versionCode)
    }

    ExpandableItem(
        expanded = expanded,
        text = { Text(text = stringResource(id = R.string.view_module_changelog)) },
        onExpandedChange = { expanded = it },
        trailingContent = {
            UpdateItemSelect(versionCode = versionCode) {
                versionCode = it.versionCode
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            viewModel.changelog?.let {
                ChangeItem(text = it)
            }
        }
    }
}

@Composable
private fun ChangeItem(
    text: String,
) = Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    shape = RoundedCornerShape(15.dp)
) {
    MarkdownText(
        modifier = Modifier
            .padding(all = 15.dp)
            .fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun UpdateItemSelect(
    versionCode: Int,
    viewModel: DetailViewModel = hiltViewModel(),
    onClick: (ModuleUpdateItem) -> Unit
) {
    val selectedValue = viewModel.versions.find {
        it.versionCode == versionCode
    } ?: viewModel.versions.first()

    var expanded by remember { mutableStateOf(false) }
    val animateZ by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        shape = RoundedCornerShape(15.dp),
        contentAlignment = Alignment.BottomEnd,
        surface = {
            FilterChip(
                selected = true,
                onClick = { expanded = true },
                label = { Text(text = selectedValue.versionCode.toString()) },
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer {
                                rotationZ = animateZ
                            },
                        painter = painterResource(id = R.drawable.arrow_down_bold),
                        contentDescription = null
                    )
                }
            )
        }
    ) {
        viewModel.versions
            .filter { it.changelog.isNotBlank() }
            .forEach {
            MenuItem(
                value = it,
                versionCode = versionCode
            ) {
                onClick(it)
                expanded = false
            }
        }
    }
}

@Composable
private fun MenuItem(
    value: ModuleUpdateItem,
    versionCode: Int,
    onClick: () -> Unit
) = DropdownMenuItem(
    modifier = Modifier
        .background(
            if (value.versionCode == versionCode) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Unspecified
            }
        ),
    text = { Text(text = value.versionCode.toString()) },
    onClick = onClick
)