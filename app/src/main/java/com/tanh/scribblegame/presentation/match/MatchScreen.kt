package com.tanh.scribblegame.presentation.match

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.presentation.match.item.MessageItem
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.util.PlayerRole

@Composable
fun MatchScreen(
    viewModel: MatchViewModel = hiltViewModel<MatchViewModel>(),
    modifier: Modifier = Modifier
) {

    val state = viewModel.state.collectAsState().value
    val messages = viewModel.messages.collectAsState().value
    val players = viewModel.players.collectAsState().value
    val match = viewModel.match.collectAsState(initial = Match()).value ?: Match()

    val snackbarHostState = remember { SnackbarHostState() }

    var inputMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(true) {
        viewModel.channel.collect { event ->
            when (event) {
                is OneTimeEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
                is OneTimeEvent.Navigate -> Unit
                is OneTimeEvent.ShowToast -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setMatchData()
    }

    LaunchedEffect(players.size) {
        if(players.size == 2) {
            //start game
            Log.d("MAT2", "RUn")
            viewModel.startGame()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            if(state.myRole == PlayerRole.DRAWING) {
//
//            } else if(state.myRole == PlayerRole.GUESSING) {
//
//            }

            //
            Text(players.toString())
            Text(match.toString())

            //chat box
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(messages) { message ->
                    MessageItem(
                        message = message,
                        userId = state.userId
                    )
                }
                item {
                    Row {
                        TextField(
                            value = inputMessage,
                            onValueChange = {
                                inputMessage = it
                            },
                            placeholder = {
                                Text(text = "Type your message")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                viewModel.onEvent(MatchEvent.OnTypeMessage(inputMessage))
                                inputMessage = ""
                            }
                        ) {
                          Icon(
                              imageVector = Icons.Default.Send,
                              contentDescription = null
                          )
                        }
                    }
                }
            }

        }
    }

}