package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.activator.regex.RegexActivationRule
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.ActivationRulesBuilder
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.hook.BeforeActionHook
import com.justai.jaicf.hook.BotHookException
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.scenario.extension.userId
import com.vitekkor.compolybot.service.CommandCoolDownService
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

abstract class BaseCommand : Scenario {
    abstract val name: String
    abstract val description: String
    open val coolDown: Duration = Duration.ofSeconds(-1)
    open val coolDownMessage: String = "Sorry, this command not available now. Try again later."

    @Autowired
    private lateinit var coolDownService: CommandCoolDownService

    abstract fun StateBuilder<BotRequest, Reactions>.commandAction()

    override val model: ScenarioModel by lazy {
        createModel {
            handle<BeforeActionHook> {
                if (!state.path.name.endsWith(name)) return@handle
                if (coolDown.isNegative || coolDown.isZero) return@handle
                if (!coolDownService.checkCommandCoolDown(name, request.userId, coolDown)) {
                    reactions.say(coolDownMessage)
                    throw BotHookException()
                }
                coolDownService.saveCommandUsageInfo(name, request.userId, coolDown)
            }
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
