package com.tanh.scribblegame.presentation.room_lists

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.util.MatchStatus

@Composable
fun MatchItem(
    match: Match,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onFullClick: () -> Unit
) {

    val matchStatus = when (match.status) {
        MatchStatus.WAITING.toString() -> "Waiting"
        MatchStatus.ONGOING.toString() -> "Ongoing"
        MatchStatus.ENDING.toString() -> "Ending"
        else -> "No status"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RectangleShape
            )
            .clickable {
                if (matchStatus == "Waiting") {
                    onClick(match.documentId)
                } else {
                    onFullClick()
                }
            }
    ) {
        Text(
            text = match.name,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = matchStatus,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }

}