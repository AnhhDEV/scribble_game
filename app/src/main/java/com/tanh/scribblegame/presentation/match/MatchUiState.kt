package com.tanh.scribblegame.presentation.match

import com.tanh.scribblegame.util.MatchStatus
import com.tanh.scribblegame.util.PlayerRole
import com.tanh.scribblegame.util.PlayerStatus

data class MatchUiState(
    //match
    val name: String = "",
    val matchStatus: String = "",
    val currentWord: String = "",
    val time: Int = 0,
    val round: Int = 0,
    val currentDrawer: String = "",

    //player
    val userId: String = "",
    val myRole: String = PlayerRole.NULL.toString(),
    val myStatus: String = PlayerStatus.ONLINE.toString(),

    //wait
    val wait: Boolean = false

)
