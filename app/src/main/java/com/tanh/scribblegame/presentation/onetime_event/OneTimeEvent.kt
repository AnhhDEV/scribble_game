package com.tanh.scribblegame.presentation.onetime_event

sealed class OneTimeEvent {
    data class Navigate(val route: String) : OneTimeEvent()
    data class ShowSnackbar(val message: String) : OneTimeEvent()
    data class ShowToast(val message: String) : OneTimeEvent()
}