package com.tanh.scribblegame.presentation.select_word

data class SelectorUiState(
    val words: List<String> = emptyList(),
    val selectedWord: String? = null,
    val time: Int = 30
)