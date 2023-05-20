package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.repository.VirtualCommandRepository
import com.vitekkor.compolybot.scenario.extension.inputText
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class DeleteVirtualCommandCommand(private val virtualCommandRepository: VirtualCommandRepository) : BaseCommand() {
    override val name: String = "удалить"
    override val description: String = "удалить виртуальную команду"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("удалить", "удалитьвиртуальнуюкоманду", "delete", "remove") }
        action {
            val commandName = request.inputText().split("\n").first().replace(Regex("^/[^ ]* ?"), "")

            if (commandName.isBlank()) {
                reactions.say("Неверные аргументы товарищ")
                return@action
            }

            val virtualCommand = virtualCommandRepository.findByIdOrNull(commandName) ?: kotlin.run {
                reactions.say("Такой виртуальной команды не существует, товарищ")
                return@action
            }

            virtualCommandRepository.delete(virtualCommand)

            reactions.say("Удалена виртуальная команда: $commandName")
        }
    }
}
