@file:Suppress("NOTHING_TO_INLINE")

package com.sanmer.mrepo.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

/**
 * A [Scrollbar] that allows for fast scrolling of content.
 * Its thumb disappears when the scrolling container is dormant.
 * @param modifier a [Modifier] for the [Scrollbar]
 * @param state the driving state for the [Scrollbar]
 * @param scrollInProgress a flag indicating if the scrolling container for the scrollbar is
 * currently scrolling
 * @param orientation the orientation of the scrollbar
 * @param onThumbDisplaced the fast scroll implementation
 */
@Composable
fun FastScrollbar(
    state: ScrollbarState,
    scrollInProgress: Boolean,
    orientation: Orientation,
    onThumbDisplaced: (Float) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: ScrollbarColors = ScrollbarDefaults.colors(),
    thumb: @Composable () -> Unit = {
        ScrollbarDefaults.Thumb(
            color = colors.thumbColor(
                scrollInProgress = scrollInProgress,
                interactionSource = interactionSource
            ),
            orientation = orientation,
        )
    }
) = Scrollbar(
    modifier = modifier,
    orientation = orientation,
    interactionSource = interactionSource,
    state = state,
    thumb = thumb,
    container = colors.trackColor(
        scrollInProgress = scrollInProgress,
        interactionSource = interactionSource
    ),
    onThumbDisplaced = onThumbDisplaced,
)

@Stable
class ScrollbarColors internal constructor(
    private val contentColor: Color,
    private val activeContentColor: Color,
    private val containerColor: Color,
    private val activeContainerColor: Color
) {
    @Composable
    private fun scrollbarColor(
        color1: Color,
        color2: Color,
        scrollInProgress: Boolean,
        interactionSource: InteractionSource
    ): Color {
        var state by remember { mutableStateOf(ThumbState.Dormant) }
        val pressed by interactionSource.collectIsPressedAsState()
        val hovered by interactionSource.collectIsHoveredAsState()
        val dragged by interactionSource.collectIsDraggedAsState()
        val active = pressed || hovered || dragged || scrollInProgress

        val color by animateColorAsState(
            targetValue = when (state) {
                ThumbState.Active -> color1
                ThumbState.Inactive -> color2
                ThumbState.Dormant -> color2.copy(0f)
            },
            animationSpec = SpringSpec(
                stiffness = Spring.StiffnessLow,
            ),
            label = "scrollbar color",
        )
        LaunchedEffect(active) {
            when (active) {
                false -> {
                    state = ThumbState.Inactive
                    delay(INACTIVE_TO_DORMANT_COOL_DOWN)
                    state = ThumbState.Dormant
                }
                true -> state = ThumbState.Active
            }
        }

        return color
    }

    @Composable
    fun thumbColor(
        scrollInProgress: Boolean,
        interactionSource: InteractionSource
    ): Color = scrollbarColor(
        color1 = activeContentColor,
        color2 = contentColor,
        scrollInProgress = scrollInProgress,
        interactionSource = interactionSource
    )

    @Composable
    fun trackColor(
        scrollInProgress: Boolean,
        interactionSource: InteractionSource
    ): Color = scrollbarColor(
        color1 = activeContainerColor,
        color2 = containerColor,
        scrollInProgress = scrollInProgress,
        interactionSource = interactionSource
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ScrollbarColors) return false

        if (contentColor != other.contentColor) return false
        if (activeContentColor != other.activeContentColor) return false
        if (containerColor != other.containerColor) return false
        if (activeContainerColor != other.activeContainerColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentColor.hashCode()
        result = 31 * result + activeContentColor.hashCode()
        result = 31 * result + containerColor.hashCode()
        result = 31 * result + activeContainerColor.hashCode()

        return result
    }

    companion object {
        private const val INACTIVE_TO_DORMANT_COOL_DOWN = 2000L
        private enum class ThumbState {
            Active, Inactive, Dormant
        }
    }
}

object ScrollbarDefaults {
    @Composable
    fun colors(
        contentColor: Color = MaterialTheme.colorScheme.primary,
        scrolledContentColor: Color = contentColor,
        containerColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
        scrolledContainerColor: Color = containerColor
    ) = ScrollbarColors(
        contentColor,
        scrolledContentColor,
        containerColor,
        scrolledContainerColor
    )

    @Composable
    fun Thumb(
        color: Color,
        orientation: Orientation,
        size: Dp = 8.dp
    ) = Box(
        modifier = Modifier
            .run {
                when (orientation) {
                    Orientation.Vertical -> width(size).fillMaxHeight()
                    Orientation.Horizontal -> height(size).fillMaxWidth()
                }
            }
            .background(
                color = color,
                shape = CircleShape
            )
    )
}

@Composable
private fun Scrollbar(
    state: ScrollbarState,
    orientation: Orientation,
    thumb: @Composable () -> Unit,
    container: Color,
    modifier: Modifier = Modifier,
    minThumbSize: Dp = 30.dp,
    maxThumbSize: Dp = 40.dp,
    interactionSource: MutableInteractionSource? = null,
    onThumbDisplaced: ((Float) -> Unit)? = null
) {
    if (state.thumbSizePercent > 0.70) return

    val minThumbSizePx = with(LocalDensity.current) { minThumbSize.toPx() }
    val maxThumbSizePx = with(LocalDensity.current) { maxThumbSize.toPx() }

    // Using Offset.Unspecified and Float.NaN instead of null
    // to prevent unnecessary boxing of primitives
    var pressedOffset by remember { mutableStateOf(Offset.Unspecified) }
    var draggedOffset by remember { mutableStateOf(Offset.Unspecified) }

    // Used to immediately show drag feedback in the UI while the scrolling implementation
    // catches up
    var interactionThumbTravelPercent by remember { mutableFloatStateOf(Float.NaN) }

    var track by remember { mutableStateOf(ScrollbarTrack(packedValue = 0)) }

    val originalThumbSizePx = state.thumbSizePercent * track.size

    val targetThumbSizePx = max(
        a = min(
            a = originalThumbSizePx,
            b = maxThumbSizePx
        ),
        b = minThumbSizePx
    )

    val reducedPercent = when {
        originalThumbSizePx > maxThumbSizePx -> originalThumbSizePx / targetThumbSizePx
        else -> 1f
    }

    val thumbTravelPercent = when {
        interactionThumbTravelPercent.isNaN() -> state.thumbDisplacementPercent * reducedPercent
        else -> interactionThumbTravelPercent
    }

    val thumbSizeDp by animateDpAsState(
        targetValue = with(LocalDensity.current) { targetThumbSizePx.toDp() },
        label = "scrollbar thumb size",
    )

    val thumbDisplacementPx = min(
        a = track.size * thumbTravelPercent,
        b = track.size - targetThumbSizePx
    )

    val draggableState = rememberDraggableState { delta ->
        if (draggedOffset == Offset.Unspecified) return@rememberDraggableState

        draggedOffset = when (orientation) {
            Orientation.Vertical -> draggedOffset.copy(y = draggedOffset.y + delta)
            Orientation.Horizontal -> draggedOffset.copy(x = draggedOffset.x + delta)
        }
    }

    // Scrollbar track container
    Box(
        modifier = modifier
            .run {
                val withHover = interactionSource?.let(::hoverable) ?: this
                when (orientation) {
                    Orientation.Vertical -> withHover.fillMaxHeight()
                    Orientation.Horizontal -> withHover.fillMaxWidth()
                }
            }
            .onGloballyPositioned { coordinates ->
                val scrollbarStartCoordinate = orientation.valueOf(coordinates.positionInRoot())
                track = ScrollbarTrack(
                    max = scrollbarStartCoordinate,
                    min = scrollbarStartCoordinate + orientation.valueOf(coordinates.size),
                )
            }
            // Process scrollbar presses
            .pointerInput(true) {
                detectTapGestures(
                    onPress = { offset ->
                        val initialPress = PressInteraction.Press(offset)
                        interactionSource?.tryEmit(initialPress)

                        // Start the press
                        pressedOffset = offset

                        interactionSource?.tryEmit(
                            when {
                                tryAwaitRelease() -> PressInteraction.Release(initialPress)
                                else -> PressInteraction.Cancel(initialPress)
                            },
                        )

                        // End the press
                        pressedOffset = Offset.Unspecified
                    },
                )
            }
            // Process scrollbar drags
            .draggable(
                state = draggableState,
                orientation = orientation,
                interactionSource = interactionSource,
                onDragStarted = { startedPosition: Offset ->
                    draggedOffset = startedPosition
                },
                onDragStopped = {
                    draggedOffset = Offset.Unspecified
                },
            )
            .background(
                color = container,
                shape = CircleShape
            ),
    ) {
        val scrollbarThumbDisplacement = max(
            a = with(LocalDensity.current) { thumbDisplacementPx.toDp() },
            b = 0.dp,
        )
        // Scrollbar thumb container
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .run {
                    when (orientation) {
                        Orientation.Horizontal -> width(thumbSizeDp)
                        Orientation.Vertical -> height(thumbSizeDp)
                    }
                }
                .offset(
                    y = when (orientation) {
                        Orientation.Horizontal -> 0.dp
                        Orientation.Vertical -> scrollbarThumbDisplacement
                    },
                    x = when (orientation) {
                        Orientation.Horizontal -> scrollbarThumbDisplacement
                        Orientation.Vertical -> 0.dp
                    },
                ),
        ) {
            thumb()
        }
    }

    if (onThumbDisplaced == null) return

    // State that will be read inside the effects that follow
    // but will not cause re-triggering of them
    val updatedState by rememberUpdatedState(state)

    // Process presses
    LaunchedEffect(pressedOffset) {
        // Press ended, reset interactionThumbTravelPercent
        if (pressedOffset == Offset.Unspecified) {
            interactionThumbTravelPercent = Float.NaN
            return@LaunchedEffect
        }

        var currentThumbDisplacement = updatedState.thumbDisplacementPercent * reducedPercent
        val destinationThumbDisplacement = track.thumbPosition(
            dimension = orientation.valueOf(pressedOffset),
        )
        val isPositive = currentThumbDisplacement < destinationThumbDisplacement
        val delta = SCROLLBAR_PRESS_DELTA * if (isPositive) 1f else -1f

        while (currentThumbDisplacement != destinationThumbDisplacement) {
            currentThumbDisplacement = when {
                isPositive -> min(
                    a = currentThumbDisplacement + delta,
                    b = destinationThumbDisplacement,
                )

                else -> max(
                    a = currentThumbDisplacement + delta,
                    b = destinationThumbDisplacement,
                )
            }

            onThumbDisplaced(currentThumbDisplacement)
            interactionThumbTravelPercent = currentThumbDisplacement
            delay(SCROLLBAR_PRESS_DELAY)
        }
    }

    // Process drags
    LaunchedEffect(draggedOffset) {
        if (draggedOffset == Offset.Unspecified) {
            interactionThumbTravelPercent = Float.NaN
            return@LaunchedEffect
        }
        val currentTravel = track.thumbPosition(
            dimension = orientation.valueOf(draggedOffset),
        )

        onThumbDisplaced(currentTravel)
        interactionThumbTravelPercent = currentTravel
    }
}

/**
 * Class definition for the core properties of a scroll bar
 */
@Immutable
@JvmInline
value class ScrollbarState internal constructor(
    internal val packedValue: Long,
) {
    companion object {
        val FULL = ScrollbarState(
            thumbSizePercent = 1f,
            thumbDisplacementPercent = 0f,
        )
    }
}

/**
 * Class definition for the core properties of a scroll bar track
 */
@Immutable
@JvmInline
private value class ScrollbarTrack(
    val packedValue: Long,
) {
    constructor(
        max: Float,
        min: Float,
    ) : this(packFloats(max, min))
}

/**
 * Creates a scrollbar state with the listed properties
 * @param thumbSizePercent the thumb size of the scrollbar as a percentage of the total track size
 * @param thumbDisplacementPercent the distance the thumb has traveled as a percentage of total
 * track size
 */
fun ScrollbarState(
    thumbSizePercent: Float,
    thumbDisplacementPercent: Float,
) = ScrollbarState(
    packFloats(
        val1 = thumbSizePercent,
        val2 = thumbDisplacementPercent,
    ),
)

/**
 * Returns the thumb size of the scrollbar as a percentage of the total track size
 */
val ScrollbarState.thumbSizePercent get() = unpackFloat1(packedValue)

/**
 * Returns the distance the thumb has traveled as a percentage of total track size
 */
val ScrollbarState.thumbDisplacementPercent get() = unpackFloat2(packedValue)

/**
 * Returns the size of the scrollbar track in pixels
 */
private val ScrollbarTrack.size get() = unpackFloat2(packedValue) - unpackFloat1(packedValue)

/**
 * Returns the position of the scrollbar thumb on the track as a percentage
 */
private fun ScrollbarTrack.thumbPosition(
    dimension: Float,
): Float = max(
    a = min(
        a = dimension / size,
        b = 1f,
    ),
    b = 0f,
)

/**
 * Returns the value of [offset] along the axis specified by [this]
 */
internal fun Orientation.valueOf(offset: Offset) = when (this) {
    Orientation.Horizontal -> offset.x
    Orientation.Vertical -> offset.y
}

/**
 * Returns the value of [intSize] along the axis specified by [this]
 */
internal fun Orientation.valueOf(intSize: IntSize) = when (this) {
    Orientation.Horizontal -> intSize.width
    Orientation.Vertical -> intSize.height
}

/**
 * Returns the value of [intOffset] along the axis specified by [this]
 */
internal fun Orientation.valueOf(intOffset: IntOffset) = when (this) {
    Orientation.Horizontal -> intOffset.x
    Orientation.Vertical -> intOffset.y
}

/**
 * Packs two Float values into one Long value for use in inline classes.
 */
private inline fun packFloats(val1: Float, val2: Float): Long {
    val v1 = val1.toBits().toLong()
    val v2 = val2.toBits().toLong()
    return v1.shl(32) or (v2 and 0xFFFFFFFF)
}

/**
 * Unpacks the first Float value in [packFloats] from its returned Long.
 */
private inline fun unpackFloat1(value: Long): Float {
    return Float.fromBits(value.shr(32).toInt())
}

/**
 * Unpacks the second Float value in [packFloats] from its returned Long.
 */
private inline fun unpackFloat2(value: Long): Float {
    return Float.fromBits(value.and(0xFFFFFFFF).toInt())
}

private const val SCROLLBAR_PRESS_DELAY = 10L
private const val SCROLLBAR_PRESS_DELTA = 0.02f