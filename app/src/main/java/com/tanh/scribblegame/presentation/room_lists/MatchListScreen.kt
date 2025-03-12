@file:Suppress("DEPRECATION")

package com.tanh.scribblegame.presentation.room_lists

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent

@Composable
fun MatchListScreen(
    viewmodel: MatchListViewModel = hiltViewModel<MatchListViewModel>(),
    modifier: Modifier = Modifier,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {


    val state = viewmodel.state.collectAsState().value
    val context = LocalContext.current
    val pullToRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isRefreshing
    )

    var showDialog by remember {
        mutableStateOf(false)
    }
    var inputName by remember {
        mutableStateOf("")
    }

    LaunchedEffect(true) {
        viewmodel.channel.collect { event ->
            when(event) {
                is OneTimeEvent.Navigate -> onNavigate(event)
                is OneTimeEvent.ShowSnackbar -> Unit
                is OneTimeEvent.ShowToast -> Unit
            }
        }
    }

    SwipeRefresh(
        state = pullToRefreshState,
        onRefresh = {
            viewmodel.onEvent(MatchListEvent.Refresh)
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(state.matches) { match ->
                    MatchItem(
                        match = match,
                        onClick = {
                            viewmodel.joinMatch(it)
                        },
                        onFullClick = {
                            Toast.makeText(context, "Match is full", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            Button(
                onClick = {
                    showDialog = !showDialog
                }
            ) {
                Text("Create a new room")
            }
            if(showDialog) {
                Dialog(
                    onDismissRequest = { showDialog = false }
                ) {
                    Column (
                        modifier = Modifier
                    ) {
                        TextField(
                            value = inputName,
                            onValueChange = { inputName = it }
                        )
                        Button(
                            onClick = {
                                viewmodel.onEvent(MatchListEvent.CreateNewRoom(inputName))
                                showDialog = false
                            }
                        ) {
                            Text("Create a new room")
                        }
                    }
                }
            }
        }
    }
}
