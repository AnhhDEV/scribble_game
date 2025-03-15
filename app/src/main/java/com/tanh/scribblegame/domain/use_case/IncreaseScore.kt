package com.tanh.scribblegame.domain.use_case

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class IncreaseScore @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, score: Int, userId: String) {
        matchRepository.updateScore(matchId, userId, score)
    }

}