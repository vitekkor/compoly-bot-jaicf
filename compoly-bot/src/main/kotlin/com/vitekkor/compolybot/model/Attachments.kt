package com.vitekkor.compolybot.model

sealed interface Attachments {
    fun isEmpty(): Boolean
}

data class TelegramAttachments(
    val photos: List<String>?,
    val video: String?,
    val replyMessage: Long?
) : Attachments {
    override fun isEmpty(): Boolean {
        return photos.isNullOrEmpty() && video.isNullOrEmpty() && replyMessage == null
    }
}

fun Attachments?.isNullOrEmpty() = this == null || isEmpty()
