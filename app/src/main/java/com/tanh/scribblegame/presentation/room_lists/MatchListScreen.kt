@file:Suppress("DEPRECATION")

package com.tanh.scribblegame.presentation.room_lists

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
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
                OneTimeEvent.PopBackStack -> Unit
            }
        }
    }

    SwipeRefresh(
        state = pullToRefreshState,
        onRefresh = {
            viewmodel.onEvent(MatchListEvent.Refresh)
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if(state.matches.isEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://signinworkspace.com/media/tojb2lmt/empty-meeting-room-pulp-fiction.gif")
                        .decoderFactory(ImageDecoderDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).weight(0.5f),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No room yet :( " +
                        "Create a new room",
                    style = MaterialTheme.typography.bodyLarge)
            } else {
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
                        modifier = Modifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                containerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                viewmodel.onEvent(MatchListEvent.CreateNewRoom(inputName))
                                showDialog = false
                            },
                            enabled = inputName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Create a new room")
                        }
                    }
                }
            }
        }
    }
}
