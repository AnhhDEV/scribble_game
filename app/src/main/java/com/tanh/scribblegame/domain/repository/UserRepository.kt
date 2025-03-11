package com.tanh.scribblegame.domain.repository

import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.User

interface UserRepository {
    suspend fun createUser(userId: String, name: String): Resources<Boolean, Exception>
    suspend fun getUser(userId: String): Resources<User, Exception>
    suspend fun deleteUser(userId: String)
}