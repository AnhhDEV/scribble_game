package com.tanh.scribblegame.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.tanh.scribblegame.data.mappers.toChatDto
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class CreateMessage @Inject constructor(
    private val matchRepository: MatchRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(matchId: String, message: Chat) {
        matchRepository.sendMessage(
            matchId = matchId,
            message = message.toChatDto()
        )
    }

}