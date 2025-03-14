package com.tanh.scribblegame.presentation.match

import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.model.Path
import com.tanh.scribblegame.domain.model.Player
import com.tanh.scribblegame.util.MatchStatus
import com.tanh.scribblegame.util.PlayerRole

data class MatchUiState(
    val matchStatus: MatchStatus = MatchStatus.WAITING,
    val userId: String = "",
    val myRole: PlayerRole = PlayerRole.NULL,
    val currentWord: String = "",
    val time: Int = 0,
    val round: Int = 0,
    val currentDrawer: String = "",
    val playersNumber: Int = 0,
)
