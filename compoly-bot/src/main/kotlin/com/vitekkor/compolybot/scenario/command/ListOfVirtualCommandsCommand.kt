package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.repository.VirtualCommandRepository
import org.springframework.stereotype.Component

@Component
class ListOfVirtualCommandsCommand(private val virtualCommandRepository: VirtualCommandRepository) : BaseCommand() {
    override val name: String = "список"
    override val description: String = "вывести список виртуальных команд"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("списоквиртуальныхкоманд", "список", "virtuallist", "list") }
        action {
            val virtualCommands = virtualCommandRepository.findAll().joinToString("\n") {
                "/${it.commandName}"
            }
            reactions.say("Виртуальные команды:\n$virtualCommands")
        }
    }
}
