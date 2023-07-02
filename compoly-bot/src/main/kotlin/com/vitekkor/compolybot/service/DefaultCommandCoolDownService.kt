package com.vitekkor.compolybot.service

import com.vitekkor.compolybot.model.CommandCoolDown
import com.vitekkor.compolybot.repository.CommandCoolDownRepository
import com.vitekkor.compolybot.scenario.command.BaseCommand
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class DefaultCommandCoolDownService(
    private val commandCoolDownRepository: CommandCoolDownRepository,
    private val ratingSystemService: RatingSystemService,
) : CommandCoolDownService {

    override fun saveCommandUsageInfo(commandName: String, userId: Long, coolDown: Duration) {
        commandCoolDownRepository.save(
            CommandCoolDown(
                userId = userId,
                commandName = commandName,
                timestamp = Instant.now().plus(coolDown).truncatedTo(ChronoUnit.MINUTES)
            )
        )
    }

    override fun checkCommandCoolDown(
        chatId: Long,
        userId: Long,
        command: BaseCommand,
    ): Boolean {
        with(command) {
            val timestamp = Instant.now().minus(coolDown)
            val maxUsageAmount =
                ratingSystemService.getMaxCommandMaxUsageAmount(chatId, userId, lvlBonus) + baseCommandUsageAmount
            return commandCoolDownRepository.findAllByUserIdAndCommandNameAndTimestampIsAfter(
                userId,
                name,
                timestamp
            ).size < maxUsageAmount
        }
    }

    override fun getNextAvailabilityTimeOfCommand(userId: Long, command: BaseCommand): Long {
        with(command) {
            val timestamp = Instant.now().minus(coolDown)
            return commandCoolDownRepository.findAllByUserIdAndCommandNameAndTimestampIsAfter(
                userId,
                name,
                timestamp
            ).firstOrNull()?.timestamp?.minusMillis(Instant.now().toEpochMilli())?.epochSecond ?: 0L
        }
    }
}
