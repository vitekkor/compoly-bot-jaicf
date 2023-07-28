package com.vitekkor.compolybot.service

import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.VirtualCommand

interface VirtualCommandsService {
    fun matches(input: String, channel: Channel): Boolean

    fun findVirtualCommand(input: String, channel: Channel): VirtualCommand?
}
