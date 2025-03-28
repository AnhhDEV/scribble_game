package com.tanh.scribblegame.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Black = Color(0xFF000000)
val LightRed = Color(0xFFF44336)
val LightGreen = Color(0xFF4CAF50)
val LightBlue = Color(0xFF2196F3)
val LightYellow = Color(0xFFFFEB3B)
val Cyan = Color(0xFF00BCD4)

val colors = listOf(
    Black,
    LightRed,
    LightGreen,
    LightBlue,
    LightYellow,
    Cyan
)

fun findColorByIndex(index: Int) = colors[index]
