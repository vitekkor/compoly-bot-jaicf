package com.vitekkor.compolybot.repository

import com.vitekkor.compolybot.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findFirstByChatIdAndUsername(chatId: Long, username: String): User?
}
