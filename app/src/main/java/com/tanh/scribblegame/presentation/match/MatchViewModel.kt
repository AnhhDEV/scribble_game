package com.tanh.scribblegame.presentation.match

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.data.resources.onError
import com.tanh.scribblegame.data.resources.onSuccess
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.use_case.use_case_manager.MessageManager
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val auth: AnonymousAuthRepository,
    private val messageManager: MessageManager
) : ViewModel() {

    private val _state = MutableStateFlow(MatchUiState())
    val state = _state.asStateFlow()

    private var matchId by mutableStateOf("")

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    private val _messages = MutableStateFlow<List<Chat>>(emptyList())
    val messages: StateFlow<List<Chat>> = _messages

    init {
        matchId = savedStateHandle.get<String>("matchId") ?: "No id"
        _state.update {
            it.copy(
                userId = auth.getCurrentUserId() ?: "No id"
            )
        }
        viewModelScope.launch {
            messageManager.observeMessages(matchId).collect { result ->
                result.onSuccess {
                    _messages.value = it
                }
                result.onError {
                    sendEvent(OneTimeEvent.ShowSnackbar(it.message ?: "Unknown error"))
                }
            }
        }
    }

    fun onEvent(event: MatchEvent) {
        when(event) {
            MatchEvent.GameStart -> gameStart()
            is MatchEvent.OnTypeMessage -> typeMessage(message = event.message)
        }

    }

    private fun gameStart() {

    }

    private fun typeMessage(message: String) {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                messageManager.createMessage(
                    matchId = matchId,
                    message = Chat(
                        content = message,
                        userId = "1",
                        time = LocalDateTime.now()
                    )
                )
            } else {
                sendEvent(OneTimeEvent.ShowSnackbar("Your device is not supported"))
            }
        }
    }



    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}