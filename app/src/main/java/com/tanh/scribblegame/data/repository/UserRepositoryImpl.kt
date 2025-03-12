package com.tanh.scribblegame.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.UserData
import com.tanh.scribblegame.domain.repository.UserRepository
import com.tanh.scribblegame.util.Collections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UserRepository {

    private val userRef = firestore.collection(Collections.USERS)

    override suspend fun createUser(userId: String, name: String): Resources<Boolean, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val user = hashMapOf(
                    "userId" to userId,
                    "name" to name
                )
                val result = suspendCoroutine<Resources<Boolean, Exception>> { continuation ->
                    userRef.add(user)
                        .addOnSuccessListener {
                            continuation.resume(Resources.Success(true))
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            continuation.resume(Resources.Error(it))
                        }
                }
                return@withContext result
            } catch (e: Exception) {
                e.printStackTrace()
                Resources.Error(e)
            }
        }
    }

    override suspend fun deleteUser(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                val userSnapshot = userRef.whereEqualTo("userId", userId).get().await()
                val docId = userSnapshot.documents.firstOrNull()?.id
                if (docId != null) {
                    userRef.document(docId).delete().await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getUser(userId: String): Resources<UserData, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val userSnapshot = userRef.whereEqualTo("userId", userId).get().await()
                val userData = userSnapshot.toObjects(UserData::class.java).firstOrNull()
                if(userData != null) {
                    Resources.Success(userData)
                } else {
                    Resources.Error(Exception("User not found"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Resources.Error(e)
            }
        }
    }

    override suspend fun getNameUser(userId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = userRef.whereEqualTo("userId", userId).get().await()
                val userData = snapshot.toObjects(UserData::class.java).firstOrNull()
                userData?.name ?: "NoName"
            } catch (e: Exception) {
                e.printStackTrace()
                "NoName"
            }
        }
    }
}