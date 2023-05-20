package com.vitekkor.compolybot.scenario.extension

import com.github.kotlintelegrambot.entities.MessageEntity
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.api.BotRequestType
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.reactions.Reactions
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

val BotRequest.chatId: Long
    get() {
        if (telegram != null) {
            return telegram!!.message.chat.id
        }
        throw IllegalStateException("Unsupported channel")
    }

val BotRequest.userId: Long
    get() {
        if (telegram != null) {
            return checkNotNull(telegram!!.message.from?.id)
        }
        throw IllegalStateException("Unsupported channel")
    }

fun Reactions.getTargetId(target: String): Long? {
    if (telegram != null) {
        val mention = telegram!!.request.message.entities?.find { it.type == MessageEntity.Type.MENTION } ?: return null
        return mention.user?.id ?: -1
    }
    return null
}

fun Reactions.selfTarget(targetId: Long) {
    if (telegram != null) {
        telegram!!.api
    }
}
