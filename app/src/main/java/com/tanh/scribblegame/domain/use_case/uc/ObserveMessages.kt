package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessages @Inject constructor(
    private val matchRepository: MatchRepository
) {

    operator fun invoke(matchId: String): Flow<Resources<List<Chat>, Exception>> {
        return matchRepository.observeMessages(matchId = matchId)
    }

}