package com.tanh.scribblegame.presentation.match

import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.toLowerCase
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.data.resources.onError
import com.tanh.scribblegame.data.resources.onSuccess
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.model.Path
import com.tanh.scribblegame.domain.model.Player
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.use_case.use_case_manager.MatchManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.MessageManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.PathManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.PlayerManager
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.util.PlayerRole
import com.tanh.scribblegame.util.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    savedStateHandle: SavedStateHandle,
    private val auth: AnonymousAuthRepository,
    private val messageManager: MessageManager,
    private val playersManager: PlayerManager,
    private val matchManager: MatchManager
) : ViewModel() {

    private val _state = MutableStateFlow(MatchUiState())
    val state = _state.asStateFlow()

    var matchId by mutableStateOf("")

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
        resetState()
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
                    result.onError {
                        sendEvent(
                            OneTimeEvent.ShowSnackbar(
                                it.message ?: "Unknown error"
                            )
                        )
                    }
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
        _state.update {
            it.copy(
                hasGameStarted = true
            )
        }
        viewModelScope.launch {
            setTimePerRound()
            selectWord()
            changeWaitingStatus()
        }
    }

    //new round start
    fun newRoundStart() {
        setRole()
        viewModelScope.launch {
            setTimePerRound()
            delay(500L)
            selectWord()
            changeWaitingStatus()
        }
    }

    private fun changeWaitingStatus() {
        if (_state.value.currentWord.isEmpty()) {
            _state.update {
                it.copy(wait = true)
            }
        } else {
            _state.update {
                it.copy(wait = false)
            }
        }
    }

    private fun setRole() {
        viewModelScope.launch {
            Log.d("MAT2", _state.value.toString())
            if (_state.value.userId.isNotBlank() && _state.value.myRole.isNotBlank()) {
                playersManager.setRolePlayer(matchId, _state.value.userId, _state.value.myRole)
            }
        }
    }

    private fun setTimePerRound() {
        _state.update {
            it.copy(
                time = 90
            )
        }
    }


    private fun decreaseTime() {
        viewModelScope.launch {
            while (state.value.time > 0) {
                _state.update {
                    it.copy(
                        time = it.time - 1
                    )
                }
                delay(1000L)
            }
        }
    }

    private fun selectWord() {
        if (_state.value.myRole == PlayerRole.DRAWING.toString() && _state.value.currentWord.isBlank()) {
            sendEvent(OneTimeEvent.Navigate(Route.SELECTOR + "/${matchId}"))
        } else if (_state.value.myRole == PlayerRole.GUESSING.toString()) {
            _state.update {
                it.copy(wait = true)
            }
        }
    }

    fun startGuess() {
        decreaseTime()
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
            guessCorrect(message)
        }
    }

    private fun guessCorrect(message: String) {
        if (message.trim().equals(_state.value.currentWord.trim(), ignoreCase = true)) {
            if (_state.value.myRole == PlayerRole.GUESSING.toString()) {
                viewModelScope.launch {
                    matchManager.updateScore(matchId, _state.value.time, _state.value.userId)
                    endRound()
                }
            }
        }
    }

    fun endRound() {
        viewModelScope.launch {
            matchManager.resetMatch(
                matchId,
                _state.value.round + 1,
                _state.value.matchStatus,
                _state.value.name
            )
        }
    }

    private fun resetState() {
        _state.value = MatchUiState()
    }

    fun backToLists() {
        viewModelScope.launch {
            resetState()
            removePlayer()
            sendEvent(OneTimeEvent.PopBackStack)
        }
    }

    private suspend fun removePlayer() {
        playersManager.deletePlayer(
            matchId = matchId,
            userId = _state.value.userId
        )
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }
}