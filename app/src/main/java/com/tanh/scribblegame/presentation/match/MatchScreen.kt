package com.tanh.scribblegame.presentation.match

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanh.scribblegame.domain.model.Player
import com.tanh.scribblegame.presentation.match.item.MessageItem
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent
import com.tanh.scribblegame.presentation.path.ColorItem
import com.tanh.scribblegame.presentation.path.DrawingAction
import com.tanh.scribblegame.presentation.path.DrawingCanvas
import com.tanh.scribblegame.presentation.path.DrawingViewModel
import com.tanh.scribblegame.ui.theme.colors
import com.tanh.scribblegame.util.PlayerRole
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    viewModel: MatchViewModel = hiltViewModel<MatchViewModel>(),
    drawingViewModel: DrawingViewModel = hiltViewModel<DrawingViewModel>(),
    modifier: Modifier = Modifier,
    onPopBackStack: () -> Unit,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel.state.collectAsState().value
    val messages = viewModel.messages.collectAsState().value
    val players = viewModel.players.collectAsState().value
    val paths = drawingViewModel.path.collectAsState().value

    val drawingState = drawingViewModel.state.collectAsState().value
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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
                OneTimeEvent.PopBackStack -> onPopBackStack()
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
        if (players.size == 2 && state.round == 1 && !state.hasGameStarted) {
            //start game
            Log.d("MAT3", "RUn")
            viewModel.startGame()
        }
    }

    LaunchedEffect(state.currentWord) {
        if (state.currentWord.isNotBlank()) {
            viewModel.startGuess()
        }
    }

    LaunchedEffect(state.round) {
        if (state.round > 1) {
            drawingViewModel.clearPaths()
            delay(1000L)
            viewModel.newRoundStart()
        }
    }

    LaunchedEffect(state.time) {
        if (state.time == 0 && state.currentWord.isNotEmpty()) {
            viewModel.endRound()
            Log.d("MAT3", "aaa")
            delay(500L)
            viewModel.startGuess()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet{
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(24.dp))
                    players.sortedByDescending { it.score }.fastForEachIndexed { idx, player ->
                        Row(
                        ) {
                            Text(
                                text = idx.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = " ${player.name}: ${player.score}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = modifier
                .padding(8.dp)
                .padding(16.dp),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            viewModel.backToLists()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = "Round: ${state.round}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.myRole == PlayerRole.DRAWING.toString()) {
                    Column(
                        modifier = Modifier.weight(3f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your role: Drawing")
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterHorizontally
                            )
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
                } else {
                    Column(
                        modifier = Modifier.weight(3f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your role: Guessing"
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        if (state.wait) {
                            Text("Waiting...")
                        }

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

                if(state.time != 0) {
                    Text(state.time.toString(), fontSize = 20.sp)
                }

                //chat box
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.Black, RectangleShape)
                        .padding(6.dp)
                        .weight(0.75f),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        MessageItem(
                            message = message,
                            userId = state.userId
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.weight(0.3f)) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = {
                            inputMessage = it
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            containerColor = Color.White
                        )
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