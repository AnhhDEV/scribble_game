package com.tanh.scribblegame.domain.model

import androidx.compose.ui.geometry.Offset

data class Path(
    val colorId: Int = 0,
    val points: List<Offset> = emptyList()
)
