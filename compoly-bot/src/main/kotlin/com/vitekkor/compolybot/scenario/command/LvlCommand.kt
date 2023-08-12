package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.scenario.extension.chatId
import com.vitekkor.compolybot.scenario.extension.userId
import com.vitekkor.compolybot.service.ratingsystem.RatingSystemService
import org.springframework.stereotype.Component

@Component
class LvlCommand(private val ratingSystemService: RatingSystemService) : BaseCommand() {
    override val name: String = "уровень"
    override val description: String = "посмотреть уровень"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("уровень", "level", "lvl") }
        action {
            val userInfo = ratingSystemService.getUserInfo(request.chatId, request.userId) ?: return@action
            val levelName = ratingSystemService.getUserLvl(userInfo).levelName
            reactions.sayAndDelete("По архивам Партии, у ${userInfo.username ?: userInfo.userId} уровень $levelName")
        }
    }
}
