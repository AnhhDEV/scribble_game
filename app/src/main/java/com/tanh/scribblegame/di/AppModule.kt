package com.tanh.scribblegame.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.domain.use_case.CreateMessage
import com.tanh.scribblegame.domain.use_case.JoinRoom
import com.tanh.scribblegame.domain.use_case.ObserveMessages
import com.tanh.scribblegame.domain.use_case.use_case_manager.MessageManager
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

}