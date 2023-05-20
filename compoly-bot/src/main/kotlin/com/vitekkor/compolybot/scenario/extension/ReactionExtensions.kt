package com.vitekkor.compolybot.scenario.extension

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.api.BotRequestType
import com.justai.jaicf.channel.telegram.telegram
import com.vitekkor.compolybot.model.Attachments
import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.TelegramAttachments

fun BotRequest.attachments(): Attachments? {
    if (telegram != null) {
        val photos = telegram!!.message.photo
            ?.sortedWith { a, b -> b.width * b.height - a.width * a.height }
            ?.first()?.fileId
            ?.let { listOf(it) }

        val video = telegram!!.message.video?.fileId
        val replyMessage = telegram!!.message.replyToMessage?.messageId
        return TelegramAttachments(photos, video, replyMessage)
    }
    return null
}

fun BotRequest.channel(): Channel {
    return Channel.TELEGRAM
}

fun BotRequest.inputText(): String {
    if (type == BotRequestType.EVENT) {
        if (telegram != null) {
            return telegram!!.message.caption.toString()
        }
    }
    return input
}
