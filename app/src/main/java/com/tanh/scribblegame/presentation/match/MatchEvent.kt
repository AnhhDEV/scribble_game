package com.tanh.scribblegame.presentation.match

sealed class MatchEvent {
    data class OnTypeMessage(val message: String) : MatchEvent()
    data object GameStart : MatchEvent()

}