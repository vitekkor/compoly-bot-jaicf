package com.vitekkor.compolybot.service.ratingsystem

import com.justai.jaicf.api.BotRequest
import com.vitekkor.compolybot.model.Level
import com.vitekkor.compolybot.model.User

interface RatingSystemService {
    fun saveUserInfo(request: BotRequest)

    fun getUserInfo(chatId: Long, username: String): User?

    fun getUserInfo(chatId: Long, userId: Long): User?

    fun getUserLvl(user: User): Level

    fun addRep(targetId: User): User

    fun subRep(targetId: User): User

    fun getMaxCommandMaxUsageAmount(chatId: Long, userId: Long, levelBonus: Int = 1): Int
}
