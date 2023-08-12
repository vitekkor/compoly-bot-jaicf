package com.vitekkor.compolybot.repository

import com.vitekkor.compolybot.model.AutoDeleteMessage
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface AutoDeleteMessagesRepository : MongoRepository<AutoDeleteMessage, String> {
    fun findAllByTimestampIsBefore(currentTime: Instant): List<AutoDeleteMessage>
}
