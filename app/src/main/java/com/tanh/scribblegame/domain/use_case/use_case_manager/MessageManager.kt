package com.tanh.scribblegame.domain.use_case.use_case_manager

import com.tanh.scribblegame.domain.use_case.uc.CreateMessage
import com.tanh.scribblegame.domain.use_case.uc.ObserveMessages
import javax.inject.Inject

class MessageManager @Inject constructor(
    val createMessage: CreateMessage,
    val observeMessages: ObserveMessages
)