package com.tanh.scribblegame.presentation.select_word

import android.provider.UserDictionary.Words
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.scribblegame.R
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
class SelectorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SelectorUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    init {
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
        sendEvent(OneTimeEvent.Navigate(Route.MATCH + "/${word}"))
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
                _state.update {
                    it.copy(
                        selectedWord = _state.value.words.random()
                    )
                }
                sendEvent(OneTimeEvent.Navigate(Route.MATCH + "/${state.value.selectedWord}"))
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