package com.tanh.scribblegame.data.model

import androidx.compose.ui.geometry.Offset

data class PathDto(
    val colorId: Int = 0,
    val points: List<String> = emptyList()
)
