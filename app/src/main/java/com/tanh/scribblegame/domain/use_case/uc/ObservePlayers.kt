package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class ObservePlayers @Inject constructor(
    private val repository: MatchRepository
) {

    operator fun invoke(matchId: String) = repository.observePlayers(matchId)

}