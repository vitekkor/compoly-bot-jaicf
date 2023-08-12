package com.vitekkor.compolybot.model.reaction

import com.justai.jaicf.logging.Reaction
import com.vitekkor.compolybot.model.Channel

data class CompolySayReaction(
    val text: String,
    override val fromState: String,
    val messageId: String,
    val chatId: Long,
    val channel: Channel,
) : Reaction(fromState)
