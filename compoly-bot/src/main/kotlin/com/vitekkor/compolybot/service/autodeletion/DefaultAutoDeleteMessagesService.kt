package com.vitekkor.compolybot.service.autodeletion

import com.github.kotlintelegrambot.network.Response
import com.vitekkor.compolybot.config.properties.BotConfigurationProperties
import com.vitekkor.compolybot.model.AutoDeleteMessage
import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.reaction.CompolySayReaction
import com.vitekkor.compolybot.repository.AutoDeleteMessagesRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@EnableScheduling
class DefaultAutoDeleteMessagesService(
    private val autoDeleteMessagesRepository: AutoDeleteMessagesRepository,
    @Qualifier("telegramClient")
    private val telegramClient: HttpClient,
    private val botConfig: BotConfigurationProperties,
) : AutoDeleteMessagesService {
    private val log = logger {}

    @Scheduled(fixedRateString = "\${bot.auto-delete-messages.fixed-rate}")
    override fun checkAndDelete() = runBlocking {
        log.info { "Start deleting messages" }
        val messagesToDelete = withContext(Dispatchers.IO) {
            autoDeleteMessagesRepository.findAllByTimestampIsBefore(Instant.now())
        }
        log.info { "Messages to delete - ${messagesToDelete.size}" }
        messagesToDelete.forEach {
            launch(Dispatchers.IO) {
                when (it.channel) {
                    Channel.TELEGRAM -> {
                        val result =
                            telegramClient.submitForm<Response<Boolean>>(
                                "https://api.telegram.org/bot${botConfig.telegramToken}/deleteMessage",
                                formParameters = Parameters.build {
                                    append("chat_id", it.chatId.toString())
                                    append("message_id", it.messageId)
                                }
                            ).result
                        if (result != true) {
                            log.error { "Couldn't delete message $it" }
                            return@launch
                        }
                        autoDeleteMessagesRepository.deleteById(it.id)
                        log.info { "Message $it was deleted successfully" }
                    }

                    else -> {
                        log.warn("Unsupported channel ${it.channel}")
                        autoDeleteMessagesRepository.deleteById(it.id)
                    }
                }
            }
        }
    }

    override fun deleteMessage(messageToDelete: CompolySayReaction, delay: Long) {
        autoDeleteMessagesRepository.save(
            AutoDeleteMessage(
                messageToDelete.text,
                messageToDelete.messageId,
                messageToDelete.chatId,
                messageToDelete.channel,
                timestamp = Instant.now().plusSeconds(delay)
            )
        )
    }
}
