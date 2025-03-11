package com.tanh.scribblegame.di

import com.tanh.scribblegame.data.repository.AnonymousAuthRepositoryImpl
import com.tanh.scribblegame.data.repository.UserRepositoryImpl
import com.tanh.scribblegame.domain.repository.AnonymousAuthRepository
import com.tanh.scribblegame.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnonymousAuthRepository(
        repository: AnonymousAuthRepositoryImpl
    ): AnonymousAuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        repository: UserRepositoryImpl
    ): UserRepository

}
