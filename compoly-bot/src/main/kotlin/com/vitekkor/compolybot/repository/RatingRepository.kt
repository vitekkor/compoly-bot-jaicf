package com.vitekkor.compolybot.repository

import com.vitekkor.compolybot.model.UserRating
import org.springframework.data.mongodb.repository.MongoRepository

interface RatingRepository: MongoRepository<UserRating, String> {
    fun findFirstByChatIdAndUsername(chatId: Long, username: String): UserRating?
}
