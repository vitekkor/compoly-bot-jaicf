package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import org.springframework.stereotype.Component

@Component
class HelpCommand(
    commands: MutableList<BaseCommand>
) : BaseCommand() {

    override val name: String = "помощь"
    override val description: String = "отображение справки (из дурки)"

    private val commandsDescription = commands
        .apply { add(this@HelpCommand) }
        .sortedBy { it.name }
        .joinToString("\n") {
            "/${it.name} — ${it.description}"
        }

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("help", "помощь") }
        action {
            reactions.say(commandsDescription)
        }
    }
}
