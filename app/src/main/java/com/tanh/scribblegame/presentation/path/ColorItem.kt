package com.tanh.scribblegame.presentation.path

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorItem(
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.size(20.dp).clip(CircleShape)
            .background(color)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = CircleShape
            )
            .clickable {
                onClick()
            }
    )

}