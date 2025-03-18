package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class ClearPaths @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String) {
        matchRepository.clearPaths(matchId)
    }

}