package com.tanh.scribblegame

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.tanh.scribblegame.data.repository.AnonymousAuthRepositoryImpl
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.repository.UserRepository
import com.tanh.scribblegame.presentation.login.LoginScreen
import com.tanh.scribblegame.presentation.match.MatchScreen
import com.tanh.scribblegame.presentation.room_lists.MatchListScreen
import com.tanh.scribblegame.presentation.select_word.SelectorScreen
import com.tanh.scribblegame.ui.theme.ScribbleGameTheme
import com.tanh.scribblegame.util.Route
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScribbleGameTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.LOGIN
                ) {
                    composable(Route.LOGIN) {
                        LoginScreen() {
                            navController.navigate(it)
                        }
                    }
                    composable(Route.SELECTOR) {
                        SelectorScreen() {
                            navController.navigate(it)
                        }
                    }
                    composable(Route.ROOMLIST) {
                        MatchListScreen() {
                            navController.navigate(it.route)
                        }
                    }
                    composable(
                        route = Route.MATCH + "/{word}",
                        arguments = listOf(
                            navArgument("word") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val word = it.arguments?.getString("word")
                        MatchScreen()
                    }
                }
            }
        }
    }

}

