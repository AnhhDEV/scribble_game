package com.tanh.scribblegame.presentation.path

import androidx.compose.ui.graphics.Color

data class DrawingState(
    val selectedColor: Int = 0,
    val currentPath: com.tanh.scribblegame.domain.model.Path? = null,
    val paths: List<com.tanh.scribblegame.domain.model.Path> = emptyList()
)
