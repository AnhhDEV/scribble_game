package com.tanh.scribblegame.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.tanh.scribblegame.data.model.ChatDto
import com.tanh.scribblegame.domain.model.Chat
import java.time.ZoneId
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
fun ChatDto.toChat(): Chat
    = Chat(
        content = content,
        userId = userId,
        time = time.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    )

@RequiresApi(Build.VERSION_CODES.O)
fun Chat.toChatDto(): ChatDto =
    ChatDto(
        content = content,
        userId = userId,
        time = Timestamp(time.toEpochSecond(ZoneOffset.UTC), 0)
    )