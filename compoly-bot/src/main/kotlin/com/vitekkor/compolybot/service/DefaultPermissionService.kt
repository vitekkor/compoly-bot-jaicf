package com.vitekkor.compolybot.service

import com.github.kotlintelegrambot.entities.ChatMember
import com.github.kotlintelegrambot.network.Response
import com.vitekkor.compolybot.config.properties.BotConfigurationProperties
import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.Role
import com.vitekkor.compolybot.model.User
import com.vitekkor.compolybot.repository.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class DefaultPermissionService(
    private val botConfig: BotConfigurationProperties,
    @Qualifier("telegramClient")
    private val client: HttpClient,
    private val userRepository: UserRepository,
) : PermissionService {
    override fun canUseCommand(commandName: String, user: User): Boolean {
        if (user.isAdmin()) return true
        return when (commandName) {
            "виртуальнаякоманда", "удалить" -> false
            else -> true
        }
    }

    override fun updateRole(user: User, channel: Channel): User {
        return runBlocking {
            when (channel) {
                Channel.TELEGRAM -> {
                    val chatInfo =
                        client.submitForm<Response<ChatMember>>(
                            "https://api.telegram.org/bot${botConfig.telegramToken}/getChatMember",
                            formParameters = Parameters.build {
                                append("chat_id", user.chatId.toString())
                                append("user_id", user.userId.toString())
                            }
                        ).result ?: return@runBlocking user
                    user.role = if (chatInfo.status == "administrator" || chatInfo.status == "creator") Role.ADMIN else Role.USER
                    return@runBlocking user
                }

                else -> error("Unsupported channel")
            }
        }
    }

    override fun setRole(user: User, newRole: Role) {
        user.role = newRole
        userRepository.save(user)
    }

    private fun User.isAdmin(): Boolean = role == Role.ADMIN
}
