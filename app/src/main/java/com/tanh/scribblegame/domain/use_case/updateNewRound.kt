package com.tanh.scribblegame.domain.use_case

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class UpdateNewRound @Inject constructor(
    private val matchRepository: MatchRepository
) {
}