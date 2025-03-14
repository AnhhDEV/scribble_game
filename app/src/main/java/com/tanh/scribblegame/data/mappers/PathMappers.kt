package com.tanh.scribblegame.data.mappers

import androidx.compose.ui.geometry.Offset
import com.tanh.scribblegame.data.model.PathDto
import com.tanh.scribblegame.domain.model.Path

//convert x,y to offset
fun String.toOffset(): Offset? {
    val values = this.trim().split(",")
    if (values.size != 2) return null

    return try {
        val xFloat = values[0].toFloat()
        val yFloat = values[1].toFloat()
        Offset(xFloat, yFloat)
    } catch (e: NumberFormatException) {
        null
    }
}

//convert string to x,y
fun Offset.toMyString(): String {
    return "${this.x},${this.y}"
}

fun PathDto.toPath(): Path =
    Path(
        colorId = colorId,
        points = points.mapNotNull { it.toOffset() }
    )

fun Path.toPathDto(): PathDto =
    PathDto(
        colorId = colorId,
        points = points.map { it.toMyString() }
    )

