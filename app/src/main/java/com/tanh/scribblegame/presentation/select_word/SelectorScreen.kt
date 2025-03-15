package com.tanh.scribblegame.presentation.select_word

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent

@Composable
fun SelectorScreen(
    viewModel: SelectorViewModel = hiltViewModel<SelectorViewModel>(),
    modifier: Modifier = Modifier,
    onPopBackStack: () -> Unit
) {

    val state = viewModel.state.collectAsState(initial = SelectorUiState()).value

    LaunchedEffect(true) {
        viewModel.channel.collect { event ->
            when(event) {
                is OneTimeEvent.Navigate -> Unit
                OneTimeEvent.PopBackStack -> onPopBackStack()
                is OneTimeEvent.ShowSnackbar -> Unit
                is OneTimeEvent.ShowToast -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.randomWords()
        viewModel.startCountDown()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.words.fastForEach {
            WordItem(
                word = it,
                modifier = Modifier.width(80.dp)
            ) {
                viewModel.selectWord(it)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = state.time.toString())
    }

}

@Composable
fun WordItem(
    word: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick
    ) {
        Text(
            text = word
        )
    }

}
