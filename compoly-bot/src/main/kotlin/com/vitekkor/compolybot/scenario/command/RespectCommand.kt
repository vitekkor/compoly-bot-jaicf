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
class RespectCommand(private val ratingSystemService: RatingSystemService) : BaseCommand() {
    override val name: String = "одобряю"
    override val description: String = "показать одобрение и повысить репутацию. TIP: /одобряю ОДОБРЯЕМЫЙ"
    override val coolDown: Duration = Duration.ofHours(1)
    override val lvlBonus: Int = 0
    override val coolDownMessage: String = "Партия не рекомендует одобрение других лиц чаще, чем раз в 1 час."

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("одобряю", "респект", "respect") }
        action {
            val target = request.input.replace("^/[^ ]* ?".toRegex(), "").trim()
            if (target.isBlank()) {
                reactions.say("Укажите одобряемого")
                return@action
            }

            if (target.removePrefix("@") == "hackGPTJAICFbot") {
                reactions.say("Мы и так знаем, что Вы, Товарищ, одобряете Нас!")
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

            ratingSystemService.addRep(targetId)

            reactions.say("Одобрение выражено")
        }
    }
}
