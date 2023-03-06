package com.sanmer.mrepo.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R

@Composable
fun ExpandableItem(
    expanded: Boolean,
    text: @Composable (() -> Unit),
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(15.dp),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val animateZ by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(shape)
                .clickable { onExpandedChange(!expanded) }
                .padding(all = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        rotationZ = animateZ
                    },
                painter = painterResource(id = R.drawable.arrow_right_bold),
                contentDescription = null
            )

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.titleMedium,
                    content = text
                )
            }
            trailingContent?.invoke()
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            content = content
        )
    }
}