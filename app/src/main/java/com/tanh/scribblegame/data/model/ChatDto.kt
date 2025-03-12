package com.tanh.scribblegame.data.model

import com.google.firebase.Timestamp
import java.time.LocalDateTime

data class ChatDto(
    val content: String = "",
    val time: Timestamp = Timestamp.now(),
    val userId: String = ""
)
