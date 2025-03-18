package com.tanh.scribblegame.domain.use_case.use_case_manager

import com.tanh.scribblegame.domain.use_case.uc.DeleteMatch
import com.tanh.scribblegame.domain.use_case.uc.IncreaseScore
import com.tanh.scribblegame.domain.use_case.uc.ObserveMatch
import com.tanh.scribblegame.domain.use_case.uc.ResetMatch
import com.tanh.scribblegame.domain.use_case.uc.UpdateMatchStatus
import com.tanh.scribblegame.domain.use_case.uc.UpdateNewRound
import com.tanh.scribblegame.domain.use_case.uc.UpdateNewWord
import javax.inject.Inject

class MatchManager @Inject constructor(
    val observeMatch: ObserveMatch,
    val updateNewWord: UpdateNewWord,
    val updateScore: IncreaseScore,
    val updateNewRound: UpdateNewRound,
    val resetMatch: ResetMatch,
    val deleteMatch: DeleteMatch,
    val updateMatchStatus: UpdateMatchStatus
)