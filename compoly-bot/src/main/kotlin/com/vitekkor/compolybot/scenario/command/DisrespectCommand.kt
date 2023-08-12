package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.scenario.extension.chatId
import com.vitekkor.compolybot.scenario.extension.userId
import com.vitekkor.compolybot.service.ratingsystem.RatingSystemService
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class DisrespectCommand(private val ratingSystemService: RatingSystemService) : BaseCommand() {
    override val name: String = "осуждаю"
    override val description: String = "показать осуждение и понизить репутацию. TIP: /осуждаю ОСУЖДАЕМЫЙ"
    override val coolDown: Duration = Duration.ofHours(1)
    override val lvlBonus: Int = 0
    override val coolDownMessage: String = "Партия не рекомендует осуждение других лиц чаще, чем раз в 1 час."

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("осуждаю", "disrespect") }
        action {
            val target = request.input.replace("^/[^ ]* ?".toRegex(), "").trim()
            if (target.isBlank()) {
                reactions.sayAndDelete("Укажите осуждаемого")
                return@action
            }

            if (target.removePrefix("@") == "hackGPTJAICFbot") {
                reactions.sayAndDelete("Отправляю чёрных воронков")
                return@action
            }

            val targetId = ratingSystemService.getUserInfo(request.chatId, target.removePrefix("@")) ?: kotlin.run {
                reactions.sayAndDelete("Этого человека нет в архивах")
                return@action
            }

            if (targetId.userId == request.userId) {
                reactions.sayAndDelete("Партия рекомендует не удалять рёбра")
                return@action
            }

            ratingSystemService.subRep(targetId)

            reactions.sayAndDelete("Осуждение выражено")
        }
    }
}
