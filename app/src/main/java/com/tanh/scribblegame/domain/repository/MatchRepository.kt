package com.tanh.scribblegame.domain.repository

import com.tanh.scribblegame.data.model.ChatDto
import com.tanh.scribblegame.data.model.PathDto
import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.Chat
import com.tanh.scribblegame.domain.model.Match
import com.tanh.scribblegame.domain.model.Player
import com.tanh.scribblegame.domain.model.UserData
import com.tanh.scribblegame.util.MatchStatus
import com.tanh.scribblegame.util.PlayerRole
import com.tanh.scribblegame.util.PlayerStatus
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    // Match
    fun getAvailableMatches(): Flow<Resources<List<Match>, Exception>>
    suspend fun createMatch(match: Match, player1: UserData): Resources<String, Exception>
    suspend fun updateMatch(matchId: String, match: Match)
    suspend fun updateMatchStatus(matchId: String, newStatus: MatchStatus)
    suspend fun updateNewWord(matchId: String, newWord: String)
    suspend fun updateRound(matchId: String, newRound: Int)
    fun observeMatch(matchId: String): Flow<Match?>
    suspend fun deleteMatch(matchId: String)

    // Players
    suspend fun adjustRole(matchId: String, userId: String, newRole: PlayerRole)
    suspend fun updateScore(matchId: String, userId: String, newScore: Int)
    suspend fun updatePlayerStatus(matchId: String, userId: String, newStatus: PlayerStatus)
    suspend fun newUserJoin(matchId: String, user: UserData): Resources<Boolean, Exception>
    suspend fun removePlayer(matchId: String, userId: String)
    suspend fun removePlayerById(matchId: String, userId: String)

    // Observers player
    suspend fun observeSpecificPlayer(matchId: String, userId: String): Flow<Resources<Player, Exception>>
    fun observePlayers(matchId: String): Flow<List<Player>>

    // Drawing
    suspend fun addPath(matchId: String, path: PathDto)
    fun observePaths(matchId: String): Flow<Resources<List<PathDto>, Exception>>
    suspend fun clearPaths(matchId: String)

    // Chats
    suspend fun sendMessage(matchId: String, message: ChatDto)
    fun observeMessages(matchId: String): Flow<Resources<List<Chat>, Exception>>

}