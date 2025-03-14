package com.tanh.scribblegame.domain.use_case.use_case_manager

import com.tanh.scribblegame.domain.use_case.IncreaseScore
import com.tanh.scribblegame.domain.use_case.ObserveMatch
import com.tanh.scribblegame.domain.use_case.UpdateNewWord
import javax.inject.Inject

class MatchManager @Inject constructor(
    val observeMatch: ObserveMatch,
    val updateNewWord: UpdateNewWord,
    val updateScore: IncreaseScore
)