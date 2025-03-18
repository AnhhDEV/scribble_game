package com.tanh.scribblegame.presentation.room_lists

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.ui.theme.LightGreen
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

    val isOngoing by remember {
        mutableStateOf(match.status == MatchStatus.ONGOING.toString())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(4.dp)
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
            color = if(isOngoing) LightGreen else Color.Red,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }

}