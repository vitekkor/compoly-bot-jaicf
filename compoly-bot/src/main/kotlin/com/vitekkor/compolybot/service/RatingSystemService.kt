package com.vitekkor.compolybot.service

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.channel.telegram.telegram
import com.vitekkor.compolybot.model.User
import com.vitekkor.compolybot.repository.UserRepository
import com.vitekkor.compolybot.scenario.extension.channel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RatingSystemService(
    private val userRepository: UserRepository,
    private val permissionService: PermissionService,
) {

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
            val user = User(userId, username, chatId, 0)
            userRepository.findByIdOrNull(user.id) ?: kotlin.run {
                permissionService.updateRole(user, request.channel())
                userRepository.save(user)
            }
        }
    }

    fun getUserInfo(chatId: Long, username: String): User? {
        return userRepository.findFirstByChatIdAndUsername(chatId, username)
    }

    fun getUserInfo(chatId: Long, userId: Long): User? {
        return userRepository.findByIdOrNull("$chatId.$userId")
    }

    fun getUserLvl(user: User): Level {
        return Level.getLevel(user.rep)
    }

    fun addRep(targetId: User): User {
        targetId.rep += baseCount
        return userRepository.save(targetId)
    }

    fun subRep(targetId: User): User {
        targetId.rep -= baseCount
        return userRepository.save(targetId)
    }

    fun getMaxCommandMaxUsageAmount(chatId: Long, userId: Long, levelBonus: Int = 1): Int {
        val userInfo = getUserInfo(chatId, userId) ?: return 0
        val level = getUserLvl(userInfo)

        return levelBonus * level.ordinal
    }
}
