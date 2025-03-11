package com.tanh.scribblegame.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.tanh.scribblegame.domain.model.User
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import javax.inject.Inject

class AnonymousAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AnonymousAuthRepository {

    override suspend fun signInAnonymously(onResult: (Boolean) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
}