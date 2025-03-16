package com.tanh.scribblegame.presentation.match

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.presentation.match.item.MessageItem
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.presentation.path.ColorItem
import com.tanh.scribblegame.presentation.path.DrawingAction
import com.tanh.scribblegame.presentation.path.DrawingCanvas
import com.tanh.scribblegame.presentation.path.DrawingViewModel
import com.tanh.scribblegame.ui.theme.colors
import com.tanh.scribblegame.ui.theme.findColorByIndex
import com.tanh.scribblegame.util.PlayerRole
import kotlinx.coroutines.delay

@Composable
fun MatchScreen(
    viewModel: MatchViewModel = hiltViewModel<MatchViewModel>(),
    drawingViewModel: DrawingViewModel = hiltViewModel<DrawingViewModel>(),
    modifier: Modifier = Modifier,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel.state.collectAsState().value
    val messages = viewModel.messages.collectAsState().value
    val players = viewModel.players.collectAsState().value
    val paths = drawingViewModel.path.collectAsState().value

    val drawingState = drawingViewModel.state.collectAsState().value

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
                is OneTimeEvent.Navigate -> {
                    onNavigate(event)
                }
                is OneTimeEvent.ShowToast -> Unit
                OneTimeEvent.PopBackStack -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setMatchData()
    }

    LaunchedEffect(viewModel.match) {
        viewModel.match.collect {
            drawingViewModel.setMatchId(it?.documentId ?: "")
        }
    }

    LaunchedEffect(players.size) {
        if(players.size == 2 && state.round == 1) {
            //start game
            Log.d("MAT2", "RUn")
            viewModel.startGame()
        }
    }

    LaunchedEffect(state.currentWord) {
        if(state.currentWord.isNotBlank()) {
            viewModel.startGuess()
        }
    }

    LaunchedEffect(state.round) {
        if(state.round > 1) {
            drawingViewModel.clearPaths()
            delay(1000L)
            viewModel.newRoundStart()
        }
    }

    Scaffold(
        modifier = modifier.padding(8.dp).padding(16.dp),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Round: ${state.round}",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if(state.myRole == PlayerRole.DRAWING.toString()) {
                Column(
                    modifier =  Modifier.height(400.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("DRAWING")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        colors.forEachIndexed { idx, color ->
                            ColorItem(
                                color,
                                onClick = {
                                    drawingViewModel.onAction(DrawingAction.OnSelectColor(idx))
                                }
                            )
                        }
                    }
                    DrawingCanvas(
                        paths = paths,
                        currentPath = drawingState.currentPath,
                        onAction = drawingViewModel::onAction,
                        isDrawingEnabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            } else  {
                Column(modifier =  Modifier.height(400.dp),
                    verticalArrangement = Arrangement.Center) {
                    Text("GUESSING")
                    Spacer(modifier = Modifier.height(6.dp))
                    DrawingCanvas(
                        paths = paths,
                        currentPath = drawingState.currentPath,
                        onAction = drawingViewModel::onAction,
                        isDrawingEnabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(state.time.toString(), fontSize = 20.sp)

            //chat box
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .border(1.dp, Color.Black, RectangleShape)
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