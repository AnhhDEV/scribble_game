package com.tanh.scribblegame.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.tanh.scribblegame.data.mappers.toChat
import com.tanh.scribblegame.data.model.ChatDto
import com.tanh.scribblegame.data.model.PathDto
import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.model.Player
import com.tanh.scribblegame.domain.model.UserData
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.util.Collections
import com.tanh.scribblegame.util.MatchStatus
import com.tanh.scribblegame.util.PlayerRole
import com.tanh.scribblegame.util.PlayerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : MatchRepository {

    private val matchesRef = firebaseFirestore.collection(Collections.MATCHES)

    override fun getAvailableMatches(): Flow<Resources<List<Match>, Exception>> {
        return callbackFlow<Resources<List<Match>, Exception>> {
            var listenerRegistration: ListenerRegistration? = null
            try {
                listenerRegistration = matchesRef
                    .orderBy("status", Query.Direction.DESCENDING)
                    .orderBy("name")
                    .addSnapshotListener { snapshot, error ->
                        val result = if (snapshot != null) {
                            val matches = snapshot.toObjects(Match::class.java).mapNotNull { it }
                            Resources.Success(matches)
                        } else {
                            Resources.Error(error ?: Exception("Not found any matches"))
                        }
                        trySend(result)
                    }
            } catch (e: Exception) {
                trySend(Resources.Error(e))
            }
            awaitClose {
                listenerRegistration?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createMatch(
        match: Match,
        player1: UserData
    ): Resources<String, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val documentId = matchesRef.document().id
                val obj = hashMapOf(
                    "currentWord" to match.currentWord,
                    "documentId" to documentId,
                    "currentDrawer" to match.currentDrawer,
                    "round" to match.round,
                    "status" to match.status,
                    "name" to match.name
                )
                matchesRef
                    .document(documentId)
                    .set(obj)
                    .await()

                val user = hashMapOf(
                    "userId" to player1.userId,
                    "name" to player1.name,
                    "score" to 0,
                    "role" to PlayerRole.GUESSING.toString(),
                    "status" to PlayerStatus.ONLINE.toString()
                )
                matchesRef
                    .document(documentId)
                    .collection(Collections.PLAYERS)
                    .add(user)
                    .await()
                delay(1000L)
                Resources.Success(documentId)
            } catch (e: Exception) {
                Resources.Error(e)
            }
        }
    }

    override suspend fun clearPaths(matchId: String) {
        withContext(Dispatchers.IO) {
            val collectionRef = matchesRef.document(matchId).collection(Collections.PATHS)
            try {
                while (true) {
                    val batch = firebaseFirestore.batch()
                    val documents = collectionRef.limit(500).get().await()
                    if(documents.isEmpty) break
                    for(document in documents) {
                        batch.delete(document.reference)
                    }
                    batch.commit().await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateMatchStatus(matchId: String, newStatus: MatchStatus) {
        withContext(Dispatchers.IO) {
            try {
                matchesRef.document(matchId).update("status", newStatus.toString()).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateNewWord(matchId: String, newWord: String) {
        withContext(Dispatchers.IO) {
            try {
                matchesRef.document(matchId).update("currentWord", newWord).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateRound(matchId: String, newRound: Int) {
        withContext(Dispatchers.IO) {
            try {
                matchesRef.document(matchId).update("round", newRound)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun observeMatch(matchId: String): Flow<Match?> {
        return callbackFlow {
            val listenerRegistration = matchesRef.document(matchId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("MATCH2", "Firestore error: ${error.message}")
                        trySend(null) // Không đóng Flow, chỉ gửi null để UI có thể xử lý
                        return@addSnapshotListener
                    }
                    val result = snapshot?.toObject(Match::class.java)
                    if (result == null) {
                        Log.w("MATCH2", "Match data is null or malformed")
                    }
                    trySend(result)
                }

            awaitClose { listenerRegistration.remove() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun adjustRole(matchId: String, userId: String, newRole: PlayerRole) {
        withContext(Dispatchers.IO) {
            try {
                //query player in match
                val result = matchesRef.document(matchId).collection(Collections.PLAYERS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                //player document id
                if (result.isEmpty) {
                    println("player not found")
                    return@withContext
                }
                result.documents.first().id.let {
                    matchesRef.document(matchId).collection(Collections.PLAYERS).document(it)
                        .update("role", newRole.toString())
                        .await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateScore(matchId: String, userId: String, newScore: Int) {
        withContext(Dispatchers.IO) {
            try {
                //query player in match
                val result = matchesRef.document(matchId).collection(Collections.PLAYERS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                //player document id
                if (result.isEmpty) {
                    println("player not found")
                    return@withContext
                }
                val playerDocId = result.documents.first().id ?: return@withContext

                matchesRef.document(matchId)
                    .collection(Collections.PLAYERS)
                    .document(playerDocId)
                    .update("score", FieldValue.increment(newScore.toLong()))
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updatePlayerStatus(
        matchId: String,
        userId: String,
        newStatus: PlayerStatus
    ) {
        withContext(Dispatchers.IO) {
            try {
                //query player in match
                val result = matchesRef.document(matchId).collection(Collections.PLAYERS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                //player document id
                if (result.isEmpty) {
                    println("player not found")
                    return@withContext
                }
                result.documents.first().id.let {
                    matchesRef.document(matchId).collection(Collections.PLAYERS).document(it)
                        .update("role", newStatus.toString())
                        .await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun newUserJoin(
        matchId: String,
        user: UserData
    ): Resources<Boolean, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val player = hashMapOf(
                    "userId" to user.userId,
                    "name" to user.name,
                    "score" to 0,
                    "role" to PlayerRole.DRAWING.toString(),
                    "status" to PlayerStatus.ONLINE.toString()
                )
                matchesRef
                    .document(matchId)
                    .collection(Collections.PLAYERS)
                    .add(player)
                    .await()
                Resources.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Resources.Error(e)
            }
        }
    }

    override suspend fun updateMatch(matchId: String, match: Match) {
        withContext(Dispatchers.IO) {
            try {
                val data = mapOf(
                    "currentDrawer" to match.currentDrawer,
                    "currentWord" to match.currentWord,
                    "round" to match.round,
                    "status" to match.status,
                    "name" to match.name
                )
                matchesRef.document(matchId).update(data).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //xoá player trong game dựa vào userId.
    override suspend fun removePlayer(matchId: String, userId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = matchesRef.document(matchId).collection(Collections.PLAYERS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                if (snapshot.isEmpty) {
                    println("Player with userId: $userId not found in match: $matchId")
                    return@withContext
                }
                val playerDocId = snapshot.documents.first()?.id ?: return@withContext
                matchesRef
                    .document(matchId)
                    .collection(Collections.PLAYERS)
                    .document(playerDocId)
                    .delete()
                    .await()
                println("Successfully removed player with userId: $userId from match: $matchId")
            } catch (e: Exception) {
                println("Failed to remove player with userId: $userId from match: $matchId")
                e.printStackTrace()
            }
        }
    }

    override suspend fun observeSpecificPlayer(
        matchId: String,
        userId: String
    ): Flow<Resources<Player, Exception>> {
        return callbackFlow<Resources<Player, Exception>> {
            var listenerRegistration: ListenerRegistration? = null
            try {
                listenerRegistration = matchesRef.document(matchId)
                    .collection(Collections.PLAYERS)
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        val result = if (error != null) {
                            Resources.Error(error = error)
                        } else {
                            val player = snapshot?.toObjects(Player::class.java)?.firstOrNull() ?: Player()
                            Resources.Success(player)
                        }
                        trySend(result)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                Resources.Error(e)
            }
            awaitClose {
                listenerRegistration?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun observePlayers(matchId: String): Flow<List<Player>> {
        return callbackFlow {
            var listenerRegistration: ListenerRegistration? = null

            try {
                listenerRegistration = matchesRef
                    .document(matchId)
                    .collection(Collections.PLAYERS)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val players = snapshot?.toObjects(Player::class.java) ?: emptyList()
                        trySend(players).isSuccess
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            awaitClose {
                listenerRegistration?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun addPath(matchId: String, path: PathDto) {
        withContext(Dispatchers.IO) {
            try {
                val pathDto = hashMapOf(
                    "colorId" to path.colorId,
                    "points" to path.points
                )

                matchesRef.document(matchId)
                    .collection(Collections.PATHS)
                    .add(pathDto)
                    .await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun observePaths(matchId: String): Flow<Resources<List<PathDto>, Exception>> {
        return callbackFlow<Resources<List<PathDto>, Exception>> {
            var listenerRegistration: ListenerRegistration? = null
            try {
                listenerRegistration = matchesRef
                    .document(matchId)
                    .collection(Collections.PATHS)
                    .addSnapshotListener { snapshot, error ->
                        val result = if (snapshot != null) {
                            val paths = snapshot.toObjects(PathDto::class.java).mapNotNull { it }
                            Resources.Success(paths)
                        } else {
                            Resources.Error(error ?: Exception("Not found any matches"))
                        }
                        trySend(result)
                    }
            } catch (e: Exception) {
                trySend(Resources.Error(e))
            }
            awaitClose {
                listenerRegistration?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun sendMessage(matchId: String, message: ChatDto) {
        withContext(Dispatchers.IO) {
            try {
                val chat = hashMapOf(
                    "content" to message.content,
                    "time" to message.time,
                    "userId" to message.userId
                )

                matchesRef.document(matchId)
                    .collection(Collections.CHATS)
                    .add(chat)
                    .await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeMessages(matchId: String): Flow<Resources<List<Chat>, Exception>> {
        return callbackFlow<Resources<List<Chat>, Exception>> {
            var listenerRegistration: ListenerRegistration? = null
            try {
                listenerRegistration = matchesRef
                    .document(matchId)
                    .collection(Collections.CHATS)
                    .orderBy("time", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        val result = if (snapshot != null) {
                            val chats = snapshot.toObjects(ChatDto::class.java).mapNotNull { it.toChat() }
                            Resources.Success(chats)
                        } else {
                            Resources.Error(error ?: Exception("Not found any matches"))
                        }
                        trySend(result)
                    }
            } catch (e: Exception) {
                trySend(Resources.Error(e))
            }
            awaitClose {
                listenerRegistration?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

}