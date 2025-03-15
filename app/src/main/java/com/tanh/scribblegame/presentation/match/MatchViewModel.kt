package com.tanh.scribblegame.presentation.match

import android.os.Build
import android.util.Log
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
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.model.Player
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.use_case.use_case_manager.MatchManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.MessageManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.PlayerManager
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.util.PlayerRole
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
    private val messageManager: MessageManager,
    private val playersManager: PlayerManager,
    private val matchManager: MatchManager
) : ViewModel() {

    private val _state = MutableStateFlow(MatchUiState())
    val state = _state.asStateFlow()

    private var matchId by mutableStateOf("")

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    private val _messages = MutableStateFlow<List<Chat>>(emptyList())
    val messages: StateFlow<List<Chat>> = _messages

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players = _players.asStateFlow()

    private val _match = MutableStateFlow<Match?>(Match())
    val match = _match.asStateFlow()

    //initial data
    init {
        matchId = savedStateHandle.get<String>("matchId") ?: ""
        if (matchId.isNotBlank()) {
            observeMatchAndMessages()
        } else {
            sendEvent(OneTimeEvent.ShowSnackbar("Match ID is invalid"))
        }
    }

    //handle event when user interact UI
    fun onEvent(event: MatchEvent) {
        when (event) {
            is MatchEvent.OnTypeMessage -> typeMessage(message = event.message)
        }

    }


    private fun observeMatchAndMessages() {
        viewModelScope.launch {
            launch {
                messageManager.observeMessages(matchId).collect { result ->
                    result.onSuccess { _messages.value = it }
                    result.onError { sendEvent(OneTimeEvent.ShowSnackbar(it.message ?: "Unknown error")) }
                }
            }
            launch {
                matchManager.observeMatch(matchId).collect { _match.value = it }
            }
            launch {
                playersManager.observePlayers(matchId)
                    .collect { playersList ->
                        _players.value = playersList

                        auth.getCurrentUserId()?.let { id ->
                            val me = playersList.find { it.userId == id }
                            _state.update {
                                it.copy(
                                    userId = id,
                                    myRole = me?.role ?: "",
                                    myStatus = me?.status ?: ""
                                )
                            }
                        }
                    }
            }
        }
    }

    //a new game start
    fun startGame() {
        //set role
        setRole()
    }

    private fun setRole() {
        viewModelScope.launch {
            Log.d("MAT2", _state.value.toString())
            if(_state.value.userId.isNotBlank() && _state.value.myRole.isNotBlank()) {
                playersManager.setRolePlayer(matchId, _state.value.userId, _state.value.myRole)
            }
        }
    }

    //init match data
    fun setMatchData() {
        viewModelScope.launch {
            match.collect { match ->
                match?.let {
                    _state.update {
                        it.copy(
                            currentDrawer = match.currentDrawer,
                            currentWord = match.currentWord,
                            round = match.round,
                            matchStatus = match.status,
                            name = match.name
                        )
                    }
                }
            }
        }
    }

    //user type message
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