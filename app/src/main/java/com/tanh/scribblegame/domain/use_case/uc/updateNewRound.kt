package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class UpdateNewRound @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, round: Int) {
        matchRepository.updateRound(matchId, round)
    }

}