package com.tanh.scribblegame.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.tanh.scribblegame.data.model.ChatDto
import com.tanh.scribblegame.data.model.PathDto
import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.model.UserData
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.util.Collections
import com.tanh.scribblegame.util.PlayerRole
import com.tanh.scribblegame.util.PlayerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : MatchRepository {

    private val matchesCollection = firebaseFirestore.collection(Collections.MATCHES)

    override fun getAvailableMatches(): Flow<Resources<List<Match>, Exception>> {
        return callbackFlow<Resources<List<Match>, Exception>> {
            var listenerRegistration: ListenerRegistration? = null
            try {
                listenerRegistration = matchesCollection
                    .orderBy("status", Query.Direction.DESCENDING)
                    .orderBy("name")
                    .addSnapshotListener { snapshot, error ->
                        val result = if(snapshot != null) {
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

    override suspend fun createMatch(match: Match, player1: UserData): Resources<Boolean, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val documentId = matchesCollection.document().id
                val obj = hashMapOf(
                    "currentWord" to match.currentWord,
                    "round" to match.round,
                    "documentId" to documentId,
                    "status" to match.status,
                    "name" to match.name
                )
                matchesCollection
                    .document(documentId)
                    .set(obj)
                    .await()

                val user = hashMapOf(
                    "userId" to player1.userId,
                    "name" to player1.name,
                    "score" to 0,
                    "role" to PlayerRole.NULL.toString(),
                    "status" to PlayerStatus.ONLINE.toString()
                )
                matchesCollection
                    .document(documentId)
                    .collection(Collections.USERS)
                    .add(user)
                    .await()
                Resources.Success(true)
            } catch (e: Exception) {
                Resources.Error(e)
            }
        }
    }

    override suspend fun updateMatchStatus(matchId: String, newStatus: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateNewWord(matchId: String, newWord: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRound(matchId: String, newRound: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun observeCurrentDrawer(matchId: String): Flow<Resources<String, Exception>> {
        TODO("Not yet implemented")
    }

    override suspend fun adjustRole(matchId: String, userId: String, newRole: PlayerRole) {
        TODO("Not yet implemented")
    }

    override suspend fun updateScore(matchId: String, userId: String, newScore: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlayerStatus(
        matchId: String,
        userId: String,
        newStatus: PlayerStatus
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun newUserJoin(
        matchId: String,
        user: UserData
    ): Resources<Boolean, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun removePlayer(matchId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun observePlayerRole(
        matchId: String,
        userId: String
    ): Flow<Resources<PlayerRole, Exception>> {
        TODO("Not yet implemented")
    }

    override suspend fun observePlayerStatus(
        matchId: String,
        userId: String
    ): Flow<Resources<PlayerStatus, Exception>> {
        TODO("Not yet implemented")
    }

    override suspend fun observePlayerScore(
        matchId: String,
        userId: String
    ): Flow<Resources<Int, Exception>> {
        TODO("Not yet implemented")
    }

    override suspend fun addPath(matchId: String, path: PathDto) {
        TODO("Not yet implemented")
    }

    override suspend fun observePaths(matchId: String): Flow<Resources<List<PathDto>, Exception>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(matchId: String, message: ChatDto) {
        TODO("Not yet implemented")
    }

    override fun observeMessages(matchId: String): Flow<Resources<List<ChatDto>, Exception>> {
        TODO("Not yet implemented")
    }



}