package com.tanh.scribblegame

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.repository.UserRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltAndroidApp
class ScribbleApplication : Application(), LifecycleObserver {

    @Inject
    lateinit var auth: AnonymousAuthRepository

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        CoroutineScope(Dispatchers.IO).launch {
            auth.signOut()
            auth.getCurrentUserId()?.let { userId ->
                userRepository.deleteUser(userId)
            }
        }
    }
}