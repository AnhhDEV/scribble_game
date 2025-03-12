package com.tanh.scribblegame.presentation.room_lists

import com.tanh.scribblegame.domain.model.Match

sealed class MatchListEvent {
    data object Refresh: MatchListEvent()
    data class CreateNewRoom(val name: String): MatchListEvent()
}