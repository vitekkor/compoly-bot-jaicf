package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.builder.createModel
import com.justai.jaicf.channel.telegram.TelegramEvent
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.context.ActionContext
import com.justai.jaicf.context.ActivatorContext
import com.justai.jaicf.hook.BeforeActionHook
import com.justai.jaicf.model.scenario.ScenarioModel
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.model.TelegramAttachments
import com.vitekkor.compolybot.model.VirtualCommand
import com.vitekkor.compolybot.model.isNullOrEmpty
import com.vitekkor.compolybot.repository.VirtualCommandRepository
import com.vitekkor.compolybot.scenario.extension.attachments
import com.vitekkor.compolybot.scenario.extension.channel
import com.vitekkor.compolybot.scenario.extension.inputText
import org.springframework.stereotype.Component

@Component
class CreateVirtualCommand(private val virtualCommandRepository: VirtualCommandRepository) : BaseCommand() {
    override val name: String = "виртуальнаякоманда"
    override val description: String = "создать виртуальную команду"
    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        val commandRegex =
            commandActivatorRegex("виртуальнаякоманда", "виртуальная_команда", "createvirtual", multiLine = true)
        activators {
            event(TelegramEvent.PHOTOS)
            event(TelegramEvent.VIDEO)
            commandActivator("виртуальнаякоманда", "виртуальная_команда", "createvirtual", multiLine = true)
        }
        action {
            if (!commandRegex.matches(request.inputText())) return@action
            val commandName = request.inputText().split("\n").first().replace(Regex("^/[^ ]* ?"), "")

            if (commandName.isBlank()) {
                reactions.say("Неверные аргументы товарищ")
                return@action
            }

            // todo isCommonCommandExists

            val attachments = request.attachments()

            val text = request.input.split("\n").drop(1)

            if (attachments.isNullOrEmpty() && text.isEmpty()) {
                reactions.say("Неверные аргументы. NB: Текст должен начинаться с новой строки")
                return@action
            }

            val virtualCommand = VirtualCommand(commandName, attachments, text.firstOrNull(), request.channel())

            virtualCommandRepository.save(virtualCommand)

            reactions.say("Добавлена команда: $commandName")
        }
    }

    override val model: ScenarioModel = createModel {
        handle<BeforeActionHook> {
            preprocess()
        }
        state(name) {
            commandAction()
        }

        state("handleVirtualAction") {
            action {
                val virtualCommand = virtualCommandRepository.findAll().find {
                    "/${it.commandName}".toRegex().matches(request.input) && request.channel() == it.channel
                }
                virtualCommand?.let {
                    processVirtualCommand(it)
                }
            }
        }
    }

    private fun ActionContext<ActivatorContext, BotRequest, Reactions>.processVirtualCommand(virtualCommand: VirtualCommand) {
        if (!virtualCommand.attachments.isNullOrEmpty()) {
            when (val attachments = virtualCommand.attachments!!) {
                is TelegramAttachments -> {
                    when {
                        attachments.photos != null -> {
                            reactions.telegram!!.sendPhoto(attachments.photos.first(), virtualCommand.text)
                        }

                        attachments.video != null -> {
                            reactions.telegram!!.sendVideo(attachments.video, caption = virtualCommand.text)
                        }

                        attachments.replyMessage != null -> {
                            reactions.telegram!!.sendMessage(
                                virtualCommand.commandName,
                                replyToMessageId = attachments.replyMessage
                            )
                        }
                    }
                }
            }
            return
        }
        reactions.say(checkNotNull(virtualCommand.text))
    }
}
