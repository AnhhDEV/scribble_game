package com.tanh.scribblegame.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanh.scribblegame.presentation.onetime_event.OneTimeEvent

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel<LoginViewModel>(),
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {

    val context = LocalContext.current
    val state = viewModel.state.collectAsState().value

    var name by remember {
        mutableStateOf("")
    }

    LaunchedEffect(true) {
        viewModel.channel.collect { event ->
            when (event) {
                is OneTimeEvent.Navigate -> onNavigate(event.route)
                is OneTimeEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.onNameChange(it)
            }
        )
        Button(
            onClick = {
                viewModel.login()
            }
        ) {
            Text(text = "Login")
        }
    }

}