package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.model.UserData
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.util.MatchStatus
import kotlinx.coroutines.delay
import javax.inject.Inject

class JoinRoom @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, userData: UserData) {
        matchRepository.newUserJoin(
            matchId = matchId,
            user = userData
        )
        delay(500L)
        matchRepository.updateMatchStatus(
            matchId = matchId,
            newStatus = MatchStatus.ONGOING
        )
    }

}