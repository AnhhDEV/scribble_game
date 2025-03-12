package com.tanh.scribblegame.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.repository.UserRepository
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.util.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AnonymousAuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun login() {
        viewModelScope.launch {
            authRepository.signInAnonymously { result ->
                if (result) {
                    sendEvent(OneTimeEvent.Navigate(Route.ROOMLIST))
                    createUser()
                } else {
                    sendEvent(OneTimeEvent.ShowToast("Login failed"))
                }
                _state.update {
                    it.copy(
                        status = result
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _state.update {
            it.copy(
                name = name
            )
        }
    }

    private fun createUser() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                userRepository.createUser(
                    userId = userId,
                    name = _state.value.name
                )
            } else {
                sendEvent(OneTimeEvent.ShowToast("User id is null"))
            }
        }
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}