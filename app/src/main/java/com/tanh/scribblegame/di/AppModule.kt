package com.tanh.scribblegame.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanh.scribblegame.domain.repository.MatchRepository
import com.tanh.scribblegame.domain.use_case.uc.ClearPaths
import com.tanh.scribblegame.domain.use_case.uc.CreateMessage
import com.tanh.scribblegame.domain.use_case.uc.DeleteMatch
import com.tanh.scribblegame.domain.use_case.uc.DeletePlayer
import com.tanh.scribblegame.domain.use_case.uc.IncreaseScore
import com.tanh.scribblegame.domain.use_case.uc.JoinRoom
import com.tanh.scribblegame.domain.use_case.uc.ObserveMatch
import com.tanh.scribblegame.domain.use_case.uc.ObserveMessages
import com.tanh.scribblegame.domain.use_case.uc.ObservePaths
import com.tanh.scribblegame.domain.use_case.uc.ObservePlayers
import com.tanh.scribblegame.domain.use_case.uc.ResetMatch
import com.tanh.scribblegame.domain.use_case.uc.SetRolePlayer
import com.tanh.scribblegame.domain.use_case.uc.UpdateMatchStatus
import com.tanh.scribblegame.domain.use_case.uc.UpdateNewPath
import com.tanh.scribblegame.domain.use_case.uc.UpdateNewRound
import com.tanh.scribblegame.domain.use_case.uc.UpdateNewWord
import com.tanh.scribblegame.domain.use_case.use_case_manager.MatchManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.MessageManager
import com.tanh.scribblegame.domain.use_case.use_case_manager.PathManager
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
        setRolePlayer: SetRolePlayer,
        deletePlayer: DeletePlayer
    ) = PlayerManager(observePlayers, setRolePlayer, deletePlayer)

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
    fun provideUpdateRound(repository: MatchRepository) = UpdateNewRound(repository)

    @Provides
    @Singleton
    fun provideResetMatch(repository: MatchRepository) = ResetMatch(repository)

    @Provides
    fun provideDeleteMatch(repository: MatchRepository) = DeleteMatch(repository)

    @Provides
    fun provideUpdateMatchStatus(repository: MatchRepository) = UpdateMatchStatus(repository)

    @Provides
    @Singleton
    fun provideMatchManager(
        observeMatch: ObserveMatch,
        updateNewWord: UpdateNewWord,
        increaseScore: IncreaseScore,
        updateNewRound: UpdateNewRound,
        resetMatch: ResetMatch,
        deleteMatch: DeleteMatch,
        updateMatchStatus: UpdateMatchStatus
    ) = MatchManager(observeMatch, updateNewWord, increaseScore, updateNewRound, resetMatch, deleteMatch, updateMatchStatus)

    @Provides
    @Singleton
    fun provideObservePathsUsecase(repository: MatchRepository) = ObservePaths(repository)

    @Provides
    @Singleton
    fun provideUpdateNewPathUsecase(repository: MatchRepository) = UpdateNewPath(repository)

    @Provides
    @Singleton
    fun provideClearPathUsecase(repository: MatchRepository) = ClearPaths(repository)

    @Provides
    @Singleton
    fun provideDeletePlayerUseCase(repository: MatchRepository) =  DeletePlayer(repository)

    @Provides
    @Singleton
    fun providePathManager(
        observePaths: ObservePaths,
        updateNewPath: UpdateNewPath,
        clearPaths: ClearPaths
    ) = PathManager(observePaths, updateNewPath, clearPaths)

}