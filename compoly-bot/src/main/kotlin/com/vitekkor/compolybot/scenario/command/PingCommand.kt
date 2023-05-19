package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.model.scenario.getValue
import org.springframework.stereotype.Component

@Component
class PingCommand : BaseCommand() {
    override val name: String = "пинг"
    override val description: String = "Pong!"
    override val model: ScenarioModel by Scenario {
        state("ping") {
            activators {
                regex("/(пинг)|(pong)")
            }
            action {
                reactions.say("Pong!")
            }
        }
    }
}
