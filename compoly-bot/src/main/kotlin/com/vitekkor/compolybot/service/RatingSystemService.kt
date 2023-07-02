package com.vitekkor.compolybot.service

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.channel.telegram.telegram
import com.vitekkor.compolybot.model.UserRating
import com.vitekkor.compolybot.repository.RatingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RatingSystemService(private val ratingRepository: RatingRepository) {

    private val baseCount = 10

    enum class Level(val levelName: String) {
        LEVEL0("ЗАСЕКРЕЧЕНО"),
        LEVEL1("октябрёнок"),
        LEVEL2("пионер"),
        LEVEL3("пролетарий"),
        LEVEL4("комсомолец"),
        LEVEL5("член профсоюза"),
        LEVEL6("посол"),
        LEVEL7("генсек"),
        LEVEL8("Гелич");

        companion object {

            private val levels = mapOf(
                Long.MIN_VALUE..-1L to LEVEL0,
                0L..20L to LEVEL1,
                21L..50L to LEVEL2,
                51L..100L to LEVEL3,
                101L..200L to LEVEL4,
                201L..500L to LEVEL5,
                501L..1000L to LEVEL6,
                1001L..5000L to LEVEL7,
                5001L..Long.MAX_VALUE to LEVEL8
            )

            fun getLevel(rep: Long): Level {
                for ((key, value) in levels) {
                    if (rep in key) return value
                }
                throw IllegalArgumentException("Can't find proper level in level map")
            }
        }
    }

    fun saveUserInfo(request: BotRequest) {
        if (request.telegram != null) {
            val userId = request.telegram!!.message.from?.id ?: return
            val username = request.telegram!!.message.from?.username
            val chatId = request.telegram!!.chatId
            val userRating = UserRating(userId, username, chatId, 0)
            ratingRepository.findByIdOrNull(userRating.id) ?: ratingRepository.save(userRating)
        }
    }

    fun getUserInfo(chatId: Long, username: String): UserRating? {
        return ratingRepository.findFirstByChatIdAndUsername(chatId, username)
    }

    fun getUserInfo(chatId: Long, userId: Long): UserRating? {
        return ratingRepository.findByIdOrNull("$chatId.$userId")
    }

    fun getUserLvl(userRating: UserRating): Level {
        return Level.getLevel(userRating.rep)
    }

    fun addRep(targetId: UserRating): UserRating {
        targetId.rep += baseCount
        return ratingRepository.save(targetId)
    }

    fun subRep(targetId: UserRating): UserRating {
        targetId.rep -= baseCount
        return ratingRepository.save(targetId)
    }

    fun getMaxCommandMaxUsageAmount(chatId: Long, userId: Long, levelBonus: Int = 1): Int {
        val userInfo = getUserInfo(chatId, userId) ?: return 0
        val level = getUserLvl(userInfo)

        return levelBonus * level.ordinal
    }
}
