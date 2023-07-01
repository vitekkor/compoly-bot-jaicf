package com.vitekkor.compolybot.service

import java.time.Duration

interface CommandCoolDownService {
    fun saveCommandUsageInfo(commandName: String, userId: Long, coolDown: Duration)

    fun checkCommandCoolDown(commandName: String, userId: Long, coolDown: Duration): Boolean
}
