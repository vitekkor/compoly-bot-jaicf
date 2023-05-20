package com.vitekkor.compolybot.scenario

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.model.scenario.getValue
import com.vitekkor.compolybot.scenario.command.BaseCommand
import org.springframework.stereotype.Component

@Component
class MainScenario(commands: List<BaseCommand>) : Scenario {
    override val model: ScenarioModel by Scenario {
        state("start") {
            activators { regex("/start") }
            action(telegram) {
                reactions.say("Привет!")
            }
        }

        commands.forEach { append(it) }

        fallback {
            reactions.go("/handleVirtualAction")
        }
    }
}