package com.tanh.scribblegame.domain.use_case

import com.tanh.scribblegame.domain.repository.MatchRepository
import javax.inject.Inject

class UpdateNewWord @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(matchId: String, newWord: String) {
        matchRepository.updateNewWord(matchId, newWord)
    }

}