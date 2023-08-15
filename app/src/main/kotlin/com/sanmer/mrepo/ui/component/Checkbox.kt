package com.sanmer.mrepo.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    TriStateCheckbox(
        state = ToggleableState(checked),
        onClick = if (onCheckedChange != null) {
            { onCheckedChange(!checked) }
        } else {
            null
        },
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource
    )
}

@Composable
fun TriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val toggleableModifier =
        if (onClick != null) {
            Modifier.triStateToggleable(
                state = state,
                onClick = onClick,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = 40.0.dp / 2 //CheckboxTokens.StateLayerSize / 2
                )
            )
        } else {
            Modifier
        }

    Box(modifier = modifier
        .then(
            if (onClick != null) {
                Modifier.minimumInteractiveComponentSize()
            } else {
                Modifier
            }
        )
        .then(toggleableModifier)
    ) {
        Crossfade(
            targetState = state,
            label = "TriStateCheckbox"
        ) {
            when (it) {
                ToggleableState.On, ToggleableState.Indeterminate -> {
                    Image(
                        painter = painterResource(id = R.drawable.tick_square_bold),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }

                ToggleableState.Off -> {
                    Image(
                        painter = painterResource(id = R.drawable.square_outline),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

private const val BoxOutDuration = 100
private const val CheckAnimationDuration = 100