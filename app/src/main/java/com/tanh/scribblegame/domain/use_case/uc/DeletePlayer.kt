package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class DeletePlayer @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, userId:  String) {
        matchRepository.removePlayerById(
            matchId = matchId,
            userId = userId
        )
    }

}