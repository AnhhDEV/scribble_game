package com.tanh.scribblegame.domain.model

data class Player(
    val userId: String = "",
    val name: String = "",
    val role: String = "",
    val status: String = "",
    val score: Int = 0
)
