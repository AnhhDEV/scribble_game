package com.tanh.scribblegame.presentation.select_word

import android.provider.UserDictionary.Words
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.R
import com.tanh.scribblegame.domain.use_case.use_case_manager.MatchManager
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.util.Route
import com.tanh.scribblegame.util.WordsUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val matchManager: MatchManager
) : ViewModel() {

    private val _state = MutableStateFlow(SelectorUiState())
    val state = _state.asStateFlow()

    var matchId by mutableStateOf("")

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    init {
        matchId = savedStateHandle.get<String>("matchId") ?: ""
        reset()
    }

    fun randomWords() {
        val words = WordsUtil.words.shuffled().take(3)
        _state.update {
            it.copy(
                words = words
            )
        }
    }

    fun selectWord(word: String) {
        _state.update {
            it.copy(
                selectedWord = word
            )
        }
        if(matchId.isNotBlank()) {
            viewModelScope.launch {
                matchManager.updateNewWord(matchId, word)
                delay(1000L)
            }
            sendEvent(OneTimeEvent.PopBackStack)
        }
    }

    fun startCountDown() {
        viewModelScope.launch {
            while(state.value.time > 0) {
                _state.update {
                    it.copy(
                        time = it.time - 1
                    )
                }
                delay(1000L)
            }
            if(state.value.time == 0) {
//                _state.update {
//                    it.copy(
//                        selectedWord = _state.value.words.random()
//                    )
//                }
                if(matchId.isNotBlank()) {
                    viewModelScope.launch {
                        matchManager.updateNewWord(matchId, _state.value.words.random())
                        delay(1000L)
                    }
                }
                sendEvent(OneTimeEvent.PopBackStack)
            }
        }
    }

    fun reset() {
        _state.update {
            it.copy(
                selectedWord = null,
                time = 30
            )
        }
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}