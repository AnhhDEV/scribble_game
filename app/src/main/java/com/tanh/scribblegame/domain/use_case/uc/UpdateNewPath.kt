package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.data.mappers.toPathDto
import com.tanh.scribblegame.domain.model.Path
import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class UpdateNewPath @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, path: Path) {
        matchRepository.addPath(matchId, path.toPathDto())
    }

}