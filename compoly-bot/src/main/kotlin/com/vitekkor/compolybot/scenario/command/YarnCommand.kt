package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.random.Random

@Component
class YarnCommand : BaseCommand() {
    override val name: String = "нить"
    override val description: String = "Да найдите же ее кто-нибудь"

    override val coolDown: Duration = Duration.ofHours(4)
    override val coolDownMessage: String =
        "Товарищ, ваши запросы на поиск нити закончились. Обновление запаса нитей происходит раз в 4 часа"
    override val baseCommandUsageAmount: Int = 6
    override val lvlBonus: Int = 2
    private var probability = 0

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("нить", "yarn") }
        action {
            probability++
            val delay = 3000L
            reactions.say("Произвожу поиск...")
            runBlocking {
                delay(delay)
                val found = Random.nextInt(0, 500)
                if (found <= probability) {
                    reactions.say("Товарищ, вы нашли нить! Этот день войдёт в историю.")
                    probability = 0
                } else {
                    reactions.say("Нить потеряна")
                }
            }
        }
    }
}
