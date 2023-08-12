package com.vitekkor.compolybot.service.autodeletion

import com.vitekkor.compolybot.model.reaction.CompolySayReaction

interface AutoDeleteMessagesService {
    fun checkAndDelete()

    fun deleteMessage(messageToDelete: CompolySayReaction, delay: Long)
}
