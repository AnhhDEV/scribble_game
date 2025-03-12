package com.tanh.scribblegame.domain.repository

import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.UserData

interface UserRepository {
    suspend fun createUser(userId: String, name: String): Resources<Boolean, Exception>
    suspend fun getUser(userId: String): Resources<UserData, Exception>
    suspend fun deleteUser(userId: String)
    suspend fun getNameUser(userId: String): String
}