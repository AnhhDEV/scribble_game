package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class ResetMatch @Inject constructor(
    private val matchRepository: MatchRepository
){

    suspend operator fun invoke(matchId: String, round: Int, status: String, name: String) {
        val match = Match(documentId = matchId, round = round, status = status, name = name)
        matchRepository.updateMatch(matchId, match)
    }

}