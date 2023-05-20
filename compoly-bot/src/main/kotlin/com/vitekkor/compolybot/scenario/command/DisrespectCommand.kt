package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.scenario.extension.chatId
import com.vitekkor.compolybot.scenario.extension.userId
import com.vitekkor.compolybot.service.RatingSystemService
import org.springframework.stereotype.Component

@Component
class DisrespectCommand(private val ratingSystemService: RatingSystemService): BaseCommand() {
    override val name: String = "осуждаю"
    override val description: String = "показать осуждение и понизить репутацию. TIP: /осуждаю ОСУЖДАЕМЫЙ"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("осуждаю", "disrespect") }
        action {
            val target = request.input.replace("^/[^ ]* ?".toRegex(), "").trim()
            if (target.isBlank()) {
                reactions.say("Укажите осуждаемого")
                return@action
            }

            if (target.removePrefix("@") == "hackGPTJAICFbot") {
                reactions.say("Отправляю чёрных воронков")
                return@action
            }

            val targetId = ratingSystemService.getUserInfo(request.chatId, target.removePrefix("@")) ?: kotlin.run {
                reactions.say("Этого человека нет в архивах")
                return@action
            }

            if (targetId.userId == request.userId) {
                reactions.say("Партия рекомендует не удалять рёбра")
                return@action
            }

            ratingSystemService.subRep(targetId)

            reactions.say("Осуждение выражено")
        }
    }
}
