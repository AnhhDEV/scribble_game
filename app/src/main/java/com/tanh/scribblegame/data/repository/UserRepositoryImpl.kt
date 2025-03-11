package com.tanh.scribblegame.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.User
import com.tanh.scribblegame.domain.repository.UserRepository
import com.tanh.scribblegame.util.Collection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UserRepository {

    private val userRef = firestore.collection(Collection.USERS)

    override suspend fun createUser(userId: String, name: String): Resources<Boolean, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val user = hashMapOf(
                    "userId" to userId,
                    "name" to name
                )
                val result = suspendCoroutine<Resources<Boolean, Exception>> { continuation ->
                    val doc = userRef.document().id
                    userRef.document(doc).set(user)
                        .addOnSuccessListener {
                            continuation.resume(Resources.Success(true))
                        }
                        .addOnFailureListener {
                            continuation.resume(Resources.Error(it))
                        }
                }
                return@withContext result
            } catch (e: Exception) {
                Resources.Error(e)
            }
        }
    }

    override suspend fun getUser(userId: String): Resources<User, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val userSnapshot = userRef.whereEqualTo("userId", userId).get().await()
                val user = userSnapshot.toObjects(User::class.java).firstOrNull()
                if(user != null) {
                    Resources.Success(user)
                } else {
                    Resources.Error(Exception("User not found"))
                }
            } catch (e: Exception) {
                Resources.Error(e)
            }
        }
    }

}