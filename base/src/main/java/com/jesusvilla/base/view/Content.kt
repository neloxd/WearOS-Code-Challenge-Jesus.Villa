package com.jesusvilla.base.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public fun Scaffold(
    modifier: Modifier = Modifier,
    vignette: @Composable (() -> Unit)? = null,
    positionIndicator: @Composable (() -> Unit)? = null,
    timeText: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
}