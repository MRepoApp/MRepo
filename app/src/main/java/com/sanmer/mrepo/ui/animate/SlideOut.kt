package com.sanmer.mrepo.ui.animate

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

object SlideOut {
    val topToBottom = slideOut(
        targetOffset = { IntOffset(0,  it.height) },
        animationSpec = tween(400)
    ) + fadeOut(
        animationSpec = tween(400)
    )

    val bottomToTop = slideOut(
        targetOffset = { IntOffset(0,  - it.height) },
        animationSpec = tween(400)
    ) + fadeOut(
        animationSpec = tween(400)
    )

    val rightToLeft = slideOut(
        targetOffset = { IntOffset(- it.width,  0) },
        animationSpec = tween(400)
    ) + fadeOut(
        animationSpec = tween(400)
    )

    val leftToRight = slideOut(
        targetOffset = { IntOffset(it.width,  0) },
        animationSpec = tween(400)
    ) + fadeOut(
        animationSpec = tween(400)
    )
}