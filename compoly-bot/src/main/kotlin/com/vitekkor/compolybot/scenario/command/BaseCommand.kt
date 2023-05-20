package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.activator.regex.RegexActivationRule
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.ActivationRulesBuilder
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.reactions.Reactions

abstract class BaseCommand : Scenario {
    abstract val name: String
    abstract val description: String

    abstract fun StateBuilder<BotRequest, Reactions>.commandAction()

    override val model: ScenarioModel by lazy {
        createModel {
            state(name) {
                commandAction()
            }
        }
    }

    companion object {
        fun ActivationRulesBuilder.commandActivator(
            vararg commands: String,
            multiLine: Boolean = false
        ): RegexActivationRule {
            return regex(commandActivatorRegex(*commands, multiLine = multiLine))
        }

        fun commandActivatorRegex(
            vararg commands: String,
            multiLine: Boolean = false
        ): Regex {
            val postfix = if (multiLine) {
                ")).*(\n.*\n?)*"
            } else {
                ")).*"
            }
            return commands.joinToString(")|(", prefix = "/((", postfix = postfix).toRegex()
        }
    }
}
