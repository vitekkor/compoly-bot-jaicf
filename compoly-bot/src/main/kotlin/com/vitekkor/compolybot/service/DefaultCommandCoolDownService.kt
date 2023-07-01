package com.vitekkor.compolybot.service

import com.vitekkor.compolybot.model.CommandCoolDown
import com.vitekkor.compolybot.repository.CommandCoolDownRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class DefaultCommandCoolDownService(
    private val commandCoolDownRepository: CommandCoolDownRepository,
) : CommandCoolDownService {

    override fun saveCommandUsageInfo(commandName: String, userId: Long, coolDown: Duration) {
        commandCoolDownRepository.save(
            CommandCoolDown(
                userId = userId,
                commandName = commandName,
                timestamp = Instant.now().plus(coolDown)
            )
        )
    }

    override fun checkCommandCoolDown(commandName: String, userId: Long, coolDown: Duration): Boolean {
        val timestamp = Instant.now().minus(coolDown)
        return commandCoolDownRepository.findAllByUserIdAndCommandNameAndTimestampIsAfter(
            userId,
            commandName,
            timestamp
        ).isEmpty()
    }
}
