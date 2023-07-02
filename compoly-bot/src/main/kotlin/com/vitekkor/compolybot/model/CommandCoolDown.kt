package com.vitekkor.compolybot.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document
data class CommandCoolDown(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val userId: Long,
    val commandName: String,
    @Indexed(expireAfterSeconds = 0)
    val timestamp: Instant,
)
