package com.tanh.scribblegame.domain.model

import com.google.firebase.Timestamp
import java.time.LocalDateTime

data class Chat(
    val content: String = "",
    val time: LocalDateTime,
    val userId: String = ""
)
