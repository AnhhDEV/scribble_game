package com.tanh.scribblegame.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.domain.use_case.CreateMessage
import com.tanh.scribblegame.domain.use_case.IncreaseScore
import com.tanh.scribblegame.domain.use_case.JoinRoom
import com.tanh.scribblegame.domain.use_case.ObserveMatch
import com.tanh.scribblegame.domain.use_case.ObserveMessages
import com.tanh.scribblegame.domain.use_case.ObservePlayers
import com.tanh.scribblegame.domain.use_case.SetRolePlayer
import com.tanh.scribblegame.domain.use_case.UpdateNewWord
import com.tanh.scribblegame.domain.use_case.use_case_manager.MatchManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.MessageManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.PlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideJoinRoomUseCase(repository: MatchRepository) = JoinRoom(repository)

    @Provides
    @Singleton
    fun provideCreateMessageUseCase(repository: MatchRepository) = CreateMessage(repository)

    @Provides
    @Singleton
    fun provideObserveMessagesUseCase(repository: MatchRepository) = ObserveMessages(repository)

    @Provides
    @Singleton
    fun provideMessageManager(
        createMessage: CreateMessage,
        observeMessages: ObserveMessages
    ) = MessageManager(createMessage, observeMessages)

    @Provides
    @Singleton
    fun provideObservePlayersUseCase(repository: MatchRepository) = ObservePlayers(repository)

    @Provides
    @Singleton
    fun provideSetRolePlayerUseCase(repository: MatchRepository) = SetRolePlayer(repository)

    @Provides
    @Singleton
    fun providePlayersManager(
        observePlayers: ObservePlayers,
        setRolePlayer: SetRolePlayer
    ) = PlayerManager(observePlayers, setRolePlayer)

    @Provides
    @Singleton
    fun provideObserveMatchUseCase(repository: MatchRepository) = ObserveMatch(repository)

    @Provides
    @Singleton
    fun provideUpdateNewWord(repository: MatchRepository) = UpdateNewWord(repository)

    @Provides
    @Singleton
    fun provideIncreaseScoreUseCase(repository: MatchRepository) = IncreaseScore(repository)

    @Provides
    @Singleton
    fun provideMatchManager(
        observeMatch: ObserveMatch,
        updateNewWord: UpdateNewWord,
        increaseScore: IncreaseScore
    ) = MatchManager(observeMatch, updateNewWord, increaseScore)

}