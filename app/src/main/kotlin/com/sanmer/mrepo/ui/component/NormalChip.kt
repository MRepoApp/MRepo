package com.sanmer.mrepo.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NormalChip(
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    shape: Shape = RoundedCornerShape(10.dp),
    enabled: Boolean = false,
    onClick: () -> Unit = {}
) = NormalChip(
    modifier = modifier,
    leadingIcon = {
        Icon(
            modifier = Modifier.size(ButtonDefaults.IconSize),
            painter = painter,
            contentDescription = null
        )
    },
    label = {
        Text(
            text = text,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    },
    shape = shape,
    onClick = onClick,
    enabled = enabled
)

@Composable
fun NormalChip(
    leadingIcon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    shape: Shape = RoundedCornerShape(10.dp),
    enabled: Boolean = false,
    onClick: () -> Unit = {}
) = Surface(
    modifier = modifier
        .clip(shape)
        .clickable(
            enabled = enabled,
            onClick = onClick
        ),
    shape = shape,
    color = containerColor,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
) {
    Row(
        modifier = Modifier.padding(all = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primary,
            content = leadingIcon
        )

        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

        ProvideTextStyle(
            value = MaterialTheme.typography.labelMedium,
            content = label
        )
    }
}