package com.tanh.scribblegame.presentation.room_lists

import com.tanh.scribblegame.domain.model.Match

data class MatchListState(
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
