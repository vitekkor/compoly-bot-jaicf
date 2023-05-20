package com.vitekkor.compolybot.model

import org.springframework.data.annotation.Id

data class VirtualCommand(
    @Id
    val commandName: String,
    val attachments: Attachments?,
    val text: String?,
    val channel: Channel
)

enum class Channel {
    TELEGRAM
}
