package com.sanmer.mrepo.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun fabPadding(all: Dp = 0.dp) = PaddingValues(
    top = all,
    bottom = all + 80.dp,
    start = all,
    end = all
)