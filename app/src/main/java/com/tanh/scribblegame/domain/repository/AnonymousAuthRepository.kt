package com.tanh.scribblegame.domain.repository

interface AnonymousAuthRepository {
    suspend fun signInAnonymously(onResult: (Boolean) -> Unit): Unit
    fun getCurrentUserId(): String?
}