package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.activator.regex.RegexActivationRule
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.ActivationRulesBuilder
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.hook.BeforeActionHook
import com.justai.jaicf.hook.BotHookException
import com.justai.jaicf.logging.currentState
import com.justai.jaicf.model.activation.disableIf
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.reaction.CompolySayReaction
import com.vitekkor.compolybot.scenario.extension.channel
import com.vitekkor.compolybot.scenario.extension.chatId
import com.vitekkor.compolybot.scenario.extension.userId
import com.vitekkor.compolybot.service.autodeletion.AutoDeleteMessagesService
import com.vitekkor.compolybot.service.cooldown.CommandCoolDownService
import com.vitekkor.compolybot.service.permission.PermissionService
import com.vitekkor.compolybot.service.ratingsystem.RatingSystemService
import com.vitekkor.compolybot.service.virtualcommand.VirtualCommandsService
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

abstract class BaseCommand : Scenario {
    abstract val name: String
    abstract val description: String
    open val coolDown: Duration = Duration.ofSeconds(-1)
    open val coolDownMessage: String = "Sorry, this command not available now. Try again later."
    open val nextAvailabilityTimeMessage: String
        get() = "Следующий вызов команды \"$name\" будет доступен через"
    open val baseCommandUsageAmount: Int = 1
    open val lvlBonus: Int = 1

    @Autowired
    private lateinit var coolDownService: CommandCoolDownService

    @Autowired
    private lateinit var permissionService: PermissionService

    @Autowired
    private lateinit var ratingSystemService: RatingSystemService

    @Autowired
    private lateinit var virtualCommandsService: VirtualCommandsService

    @Autowired
    private lateinit var autoDeleteMessagesService: AutoDeleteMessagesService

    abstract fun StateBuilder<BotRequest, Reactions>.commandAction()

    protected fun BeforeActionHook.preprocess() {
        val user = checkNotNull(ratingSystemService.getUserInfo(request.chatId, request.userId))
        if (!state.path.name.endsWith(name)) return
        if (!permissionService.canUseCommand(name, user)) throw BotHookException()
        if (coolDown.isNegative || coolDown.isZero) return
        if (!coolDownService.checkCommandCoolDown(request.chatId, request.userId, this@BaseCommand)) {
            val timeLeft = coolDownService.getNextAvailabilityTimeOfCommand(request.userId, this@BaseCommand)
            val timeLeftString =
                String.format("%d:%02d:%02d", timeLeft / 3600, timeLeft % 3600 / 60, timeLeft % 3600 % 60)
            reactions.sayAndDelete("$coolDownMessage $nextAvailabilityTimeMessage $timeLeftString.")
            throw BotHookException()
        }
        coolDownService.saveCommandUsageInfo(name, request.userId, coolDown)
    }

    override val model: ScenarioModel by lazy {
        createModel {
            handle<BeforeActionHook> {
                preprocess()
            }
            state(name) {
                commandAction()
            }
        }
    }

    fun ActivationRulesBuilder.commandActivator(
        vararg commands: String,
        multiLine: Boolean = false
    ): RegexActivationRule {
        return regex(commandActivatorRegex(*commands, multiLine = multiLine)).disableIf {
            virtualCommandsService.matches(request.input, request.channel())
        } as RegexActivationRule
    }

    fun Reactions.sayAndDelete(text: String, delay: Long = 60): CompolySayReaction {
        return when {
            telegram != null -> {
                val chatId = telegram!!.chatId
                val result = telegram!!.api.sendMessage(chatId, text, null, null, null, null, null)
                checkNotNull(result.first?.body()?.result) { "Couldn't send message to chat $chatId: ${result.second}" }.let {
                    CompolySayReaction(text, currentState, it.messageId.toString(), it.chat.id, Channel.TELEGRAM).register()
                }.also { autoDeleteMessagesService.deleteMessage(it, delay) }
            }

            else -> error("Unsupported channel")
        }
    }

    companion object {
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
