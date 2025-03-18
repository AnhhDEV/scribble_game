package com.tanh.scribblegame.domain.use_case.use_case_manager

import com.tanh.scribblegame.domain.use_case.uc.DeletePlayer
import com.tanh.scribblegame.domain.use_case.uc.ObservePlayers
import com.tanh.scribblegame.domain.use_case.uc.SetRolePlayer
import javax.inject.Inject

class PlayerManager @Inject constructor(
    val observePlayers: ObservePlayers,
    val setRolePlayer: SetRolePlayer,
    val deletePlayer: DeletePlayer
)