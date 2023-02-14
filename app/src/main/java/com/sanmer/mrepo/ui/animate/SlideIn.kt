package com.sanmer.mrepo.ui.animate

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.ui.unit.IntOffset

object SlideIn {
    val bottomToTop = slideIn(
        initialOffset = { IntOffset(0,  it.height) },
        animationSpec = tween(400)
    ) + fadeIn(
        animationSpec = tween(400)
    )

    val topToBottom = slideIn(
        initialOffset = { IntOffset(0,  - it.height) },
        animationSpec = tween(400)
    ) + fadeIn(
        animationSpec = tween(400)
    )

    val leftToRight = slideIn(
        initialOffset = { IntOffset(- it.width, 0) },
        animationSpec = tween(400)
    ) + fadeIn(
        animationSpec = tween(400)
    )

    val rightToLeft = slideIn(
        initialOffset = { IntOffset(it.width, 0) },
        animationSpec = tween(400)
    ) + fadeIn(
        animationSpec = tween(400)
    )
}