package com.sanmer.mrepo.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [Segmented buttons](https://m3.material.io/components/segmented-buttons/overview)
 */
@Composable
fun SegmentedButtons(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    shape: Shape = SegmentedButtonsDefaults.Shape,
    border: BorderStroke = SegmentedButtonsDefaults.Border,
    segments: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .selectableGroup()
            .defaultMinSize(minHeight = 40.dp),
        shape = shape,
        contentColor = contentColor,
        color = containerColor,
        border = border
    ) {
        SubcomposeLayout { constraints ->
            val segmentedButtonsWidth = constraints.maxWidth
            val segmentsMeasurable = subcompose(SegmentSlots.Segment, segments)
            val segmentCount = segmentsMeasurable.size
            val segmentWidth = (segmentedButtonsWidth / segmentCount)
            val segmentsPlaceable = segmentsMeasurable.map {
                it.measure(constraints.copy(minWidth = segmentWidth, maxWidth = segmentWidth))
            }
            val segmentedButtonsHeight = segmentsPlaceable.maxByOrNull { it.height }?.height ?: 0

            val divider = SegmentedButtonsDefaults.divider(
                height = segmentedButtonsHeight.toDp(),
                border = border
            )
            val dividerWidth = border.width.roundToPx()

            layout(segmentedButtonsWidth, segmentedButtonsHeight) {
                segmentsPlaceable.forEachIndexed { index, placeableItem ->
                    placeableItem.placeRelative(index * segmentWidth + dividerWidth, 0)

                    if (index == segmentCount - 1) return@forEachIndexed

                    val dividerMeasurable = subcompose(index, divider)
                    val placeableDivider =
                        dividerMeasurable.first().measure(constraints.copy(minWidth = 0))

                    placeableDivider.placeRelative(
                        (index + 1) * segmentWidth,
                        0
                    )
                }
            }
        }
    }
}

@Composable
fun Segment(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SegmentedButtonColors = SegmentedButtonsDefaults.buttonColor(),
    contentPadding: PaddingValues = SegmentedButtonsDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val containerColor by colors.containerColor(selected)
    val contentColor by colors.contentColor(selected)

    Surface(
        modifier = modifier
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                )
            ),
        color = containerColor,
        contentColor = contentColor,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                Row(
                    modifier = Modifier.padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

private enum class SegmentSlots {
    Segment,
}

object SegmentedButtonsDefaults {
    private val borderColor @Composable get() = MaterialTheme.colorScheme.outline
    val Shape: Shape = CircleShape
    val Border @Composable get() = BorderStroke(1.dp, borderColor)
    val ContentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp)

    @Composable
    fun buttonColor() = SegmentedButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = MaterialTheme.colorScheme.onSurface
    )

    fun divider(
        height: Dp,
        border: BorderStroke
    ) = @Composable {
        Canvas(
            modifier = Modifier
                .height(height)
                .width(border.width)
        ) {
            drawLine(
                brush = border.brush,
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height),
                strokeWidth  = size.width
            )
        }
    }
}

@Immutable
class SegmentedButtonColors internal constructor(
    private val containerColor: Color,
    private val contentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color,
) {
    @Composable
    internal fun containerColor(selected: Boolean): State<Color> {
        return rememberUpdatedState(if (selected) containerColor else disabledContainerColor)
    }

    @Composable
    internal fun contentColor(selected: Boolean): State<Color> {
        return rememberUpdatedState(if (selected) contentColor else disabledContentColor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SegmentedButtonColors) return false

        if (containerColor != other.containerColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledContentColor != other.disabledContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
        return result
    }
}

@Preview
@Composable
fun SegmentedButtonsPreview() {
   Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.padding(all = 10.dp)) {
            SegmentedButtons {
                Segment(
                    selected = true,
                    onClick = { }
                ) {
                    Text(text = "CLOUD")
                }

                Segment(
                    selected = false,
                    onClick = {}
                ) {
                    Text(text = "INSTALLED")
                }

                Segment(
                    selected = false,
                    onClick = {}
                ) {
                    Text(text = "UPDATES")
                }
            }
        }
    }
}