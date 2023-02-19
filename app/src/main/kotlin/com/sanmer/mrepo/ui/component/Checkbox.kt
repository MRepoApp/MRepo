package com.sanmer.mrepo.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
    val alpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    Box(modifier = modifier
        .then(if (onCheckedChange != null) {
            Modifier.triStateToggleable(
                state = ToggleableState(checked),
                onClick = { onCheckedChange(!checked) },
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = 20.dp
                )
            )
        } else {
            Modifier
        }),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tick_square_bold),
            contentDescription = null,
            alpha = alpha,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Image(
            painter = painterResource(id = R.drawable.square_outline),
            contentDescription = null,
            alpha = 1f - alpha,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}