package com.tanh.scribblegame.presentation.path

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastForEach
import com.tanh.scribblegame.domain.model.Path
import com.tanh.scribblegame.ui.theme.findColorByIndex
import kotlin.math.abs

@Composable
fun DrawingCanvas(
    paths: List<Path>,
    currentPath: Path?,
    onAction: (DrawingAction) -> Unit,
    isDrawingEnabled: Boolean,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(Color.White)
            .pointerInput(true) {
                if(isDrawingEnabled) {
                    detectDragGestures(
                        onDragStart = {
                            onAction(DrawingAction.OnNewPathStart)
                        },
                        onDragEnd = {
                            onAction(DrawingAction.OnPathEnd)
                        },
                        onDrag = { change, _ ->
                            onAction(DrawingAction.OnDraw(change.position))
                        },
                        onDragCancel = {
                            onAction(DrawingAction.OnPathEnd)
                        }
                    )
                    detectTapGestures(
                        onTap = {
                            onAction(DrawingAction.OnDraw(it))
                        }
                    )
                }
            }
    ) {
        paths.fastForEach { pathData ->
            drawMyPath(
                paths = pathData.points,
                colorIdx = pathData.colorId
            )
        }
        currentPath?.let {
            drawMyPath(
                paths = it.points,
                colorIdx = it.colorId
            )
        }
    }

}

fun DrawScope.drawMyPath(
    paths: List<Offset>,
    colorIdx: Int,
    thickness: Float = 10f
) {
    val smoothedPath = androidx.compose.ui.graphics.Path().apply  {
        if(paths.isNotEmpty()) {
            moveTo(paths.first().x, paths.first().y)
            val smoothness = 5
            for(i in 1..paths.lastIndex) {
                val from = paths[i - 1]
                val to = paths[i]
                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)
                if(dx >= smoothness || dy >= smoothness) {
                    quadraticTo(
                        x1 = (from.x + to.x) / 2f,
                        y1 = (from.y + to.y) / 2f,
                        x2 = to.x,
                        y2 = to.y
                    )
                }
            }
        }
    }

    drawPath(
        path = smoothedPath,
        color = findColorByIndex(colorIdx),
        style = Stroke(
            width = thickness,
            cap = StrokeCap.Round,  //first path
            join = StrokeJoin.Round //end path
        )
    )

    if(paths.size == 1) {
        drawCircle(
            color = findColorByIndex(colorIdx),
            radius = 5f,
            center = paths.first()
        )
    }
}