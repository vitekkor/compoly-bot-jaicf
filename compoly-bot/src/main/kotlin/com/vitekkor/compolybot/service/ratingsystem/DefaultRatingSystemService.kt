package com.vitekkor.compolybot.service.ratingsystem

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.channel.telegram.telegram
import com.vitekkor.compolybot.model.Level
import com.vitekkor.compolybot.model.User
import com.vitekkor.compolybot.repository.UserRepository
import com.vitekkor.compolybot.scenario.extension.channel
import com.vitekkor.compolybot.service.permission.PermissionService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DefaultRatingSystemService(
    private val userRepository: UserRepository,
    private val permissionService: PermissionService,
) : RatingSystemService {

    private val baseCount = 10

    override fun saveUserInfo(request: BotRequest) {
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

    override fun getUserInfo(chatId: Long, username: String): User? {
        return userRepository.findFirstByChatIdAndUsername(chatId, username)
    }

    override fun getUserInfo(chatId: Long, userId: Long): User? {
        return userRepository.findByIdOrNull("$chatId.$userId")
    }

    override fun getUserLvl(user: User): Level {
        return Level.getLevel(user.rep)
    }

    override fun addRep(targetId: User): User {
        targetId.rep += baseCount
        return userRepository.save(targetId)
    }

    override fun subRep(targetId: User): User {
        targetId.rep -= baseCount
        return userRepository.save(targetId)
    }

    override fun getMaxCommandMaxUsageAmount(chatId: Long, userId: Long, levelBonus: Int): Int {
        val userInfo = getUserInfo(chatId, userId) ?: return 0
        val level = getUserLvl(userInfo)

        return levelBonus * level.ordinal
    }
}
