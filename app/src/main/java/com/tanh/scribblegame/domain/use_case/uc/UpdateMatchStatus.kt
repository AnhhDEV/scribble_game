package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.util.MatchStatus
import javax.inject.Inject

class UpdateMatchStatus @Inject constructor(
    private val repository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, status: MatchStatus) {
        repository.updateMatchStatus(matchId, status)
    }

}