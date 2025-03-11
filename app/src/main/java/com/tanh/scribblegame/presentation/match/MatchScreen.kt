package com.tanh.scribblegame.presentation.match

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MatchScreen(
    viewModel: MatchViewModel = hiltViewModel<MatchViewModel>(),
    modifier: Modifier = Modifier
) {

    val value = viewModel.word.collectAsState().value

    Text(
        text = value
    )

}