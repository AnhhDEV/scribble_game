package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.util.PlayerRole
import javax.inject.Inject

class SetRolePlayer @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(
        matchId: String,
        userId: String,
        playerRole: String
    ) {
        if(playerRole == PlayerRole.DRAWING.toString()) {
            matchRepository.adjustRole(matchId, userId, PlayerRole.GUESSING)
        } else if(playerRole == PlayerRole.GUESSING.toString()) {
            matchRepository.adjustRole(matchId, userId, PlayerRole.DRAWING)
        }
    }

}