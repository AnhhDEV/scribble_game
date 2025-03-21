package com.tanh.scribblegame.presentation.room_lists

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.data.resources.onError
import com.tanh.scribblegame.data.resources.onSuccess
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.model.UserData
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.domain.repository.UserRepository
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.domain.use_case.uc.JoinRoom
import com.tanh.scribblegame.util.MatchStatus
import com.tanh.scribblegame.util.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchListViewModel @Inject constructor(
    private val repository: MatchRepository,
    private val auth: AnonymousAuthRepository,
    private val userDataRepository: UserRepository,
    private val joinRoom: JoinRoom
) : ViewModel() {

    private val _state = MutableStateFlow(MatchListState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    init {
        fetchMatches()
    }

    fun onEvent(event: MatchListEvent) {
        when (event) {
            MatchListEvent.Refresh -> fetchMatches()
            is MatchListEvent.CreateNewRoom -> createNewRoom(event.name)
        }
    }

    private fun createNewRoom(name: String) {
        viewModelScope.launch {
            val match = Match(
                name = name,
                status = MatchStatus.WAITING.toString()
            )
            val userId = auth.getCurrentUserId() ?: ""
            val username = async {
                userDataRepository.getNameUser(userId)
            }.await()
            val userData = UserData(
                userId = userId,
                name = username
            )
            val result = async {
                repository.createMatch(match, userData)
            }.await()
            result.let {
                it.onSuccess { matchId ->
                    sendEvent(OneTimeEvent.Navigate(Route.MATCH + "/$matchId"))
                }
                it.onError { error ->
                    _state.update { list ->
                        list.copy(error = error.message)
                    }
                }
            }

        }
    }

    private fun fetchMatches() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            repository.getAvailableMatches().collect { result ->
                result.onSuccess { matches ->
                    _state.update {
                        it.copy(matches = matches, isLoading = false)
                    }
                }
                result.onError { error ->
                    _state.update {
                        it.copy(error = error.message, isLoading = false)
                    }
                }
            }
        }
    }

    fun joinMatch(matchId: String) {
        viewModelScope.launch {
            val userId = auth.getCurrentUserId()
            if (userId == null) {
                Log.d("joinMatch", "User not logged in")
                return@launch
            }
            userDataRepository.getUser(userId)
                .onSuccess { user ->
                    launch {
                        joinRoom(matchId, user)
                        sendEvent(OneTimeEvent.Navigate(Route.MATCH + "/$matchId"))
                    }
                }
                .onError {
                    Log.d("joinMatch", "Failed to fetch user: ${it.message}")
                }
        }
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}