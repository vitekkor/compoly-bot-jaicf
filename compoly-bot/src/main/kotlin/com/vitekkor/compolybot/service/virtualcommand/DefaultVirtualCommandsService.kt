package com.vitekkor.compolybot.service.virtualcommand

import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.VirtualCommand
import com.vitekkor.compolybot.repository.VirtualCommandRepository
import org.springframework.stereotype.Service

@Service
class DefaultVirtualCommandsService(
    private val virtualCommandRepository: VirtualCommandRepository,
) : VirtualCommandsService {
    override fun matches(input: String, channel: Channel): Boolean {
        return findVirtualCommand(input, channel) != null
    }

    override fun findVirtualCommand(input: String, channel: Channel): VirtualCommand? {
        return virtualCommandRepository.findAll().find {
            "/${it.commandName}".toRegex().matches(input) && channel == it.channel
        }
    }
}
