package com.tanh.scribblegame.domain.use_case.use_case_manager

import android.os.Build
import androidx.annotation.RequiresApi
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.use_case.CreateMessage
import com.tanh.scribblegame.domain.use_case.ObserveMessages
import javax.inject.Inject

class MessageManager @Inject constructor(
    val createMessage: CreateMessage,
    val observeMessages: ObserveMessages
)