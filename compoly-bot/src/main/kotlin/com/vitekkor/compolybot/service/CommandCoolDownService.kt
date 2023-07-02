package com.vitekkor.compolybot.service

import com.vitekkor.compolybot.scenario.command.BaseCommand
import java.time.Duration

interface CommandCoolDownService {
    fun saveCommandUsageInfo(commandName: String, userId: Long, coolDown: Duration)

    fun checkCommandCoolDown(
        chatId: Long,
        userId: Long,
        command: BaseCommand,
    ): Boolean
}
