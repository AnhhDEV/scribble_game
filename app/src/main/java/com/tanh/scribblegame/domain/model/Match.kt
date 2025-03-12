package com.tanh.scribblegame.domain.model

import com.tanh.scribblegame.util.MatchStatus

data class Match(
    val currentWord: String = "",
    val documentId: String = "",
    val round: Int = 0,
    val name: String = "",
    val status: String = "",
    val currentDrawer: String = ""
)
