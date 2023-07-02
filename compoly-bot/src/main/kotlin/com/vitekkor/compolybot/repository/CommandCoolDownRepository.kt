package com.vitekkor.compolybot.repository

import com.vitekkor.compolybot.model.CommandCoolDown
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface CommandCoolDownRepository : MongoRepository<CommandCoolDown, String> {
    fun findAllByUserIdAndCommandNameAndTimestampIsAfter(
        userId: Long,
        commandName: String,
        timestamp: Instant,
    ): List<CommandCoolDown>
}
