package com.tanh.scribblegame.presentation.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


}