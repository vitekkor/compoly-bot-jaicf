package com.vitekkor.compolybot.model

import org.springframework.data.annotation.Id
import java.time.Instant

data class AutoDeleteMessage(
    val text: String,
    val messageId: String,
    val chatId: Long,
    val channel: Channel,
    @Id
    val id: String = "$channel.$chatId.$messageId",
    val timestamp: Instant,
)
