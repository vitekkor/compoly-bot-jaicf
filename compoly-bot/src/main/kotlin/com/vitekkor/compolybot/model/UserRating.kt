package com.vitekkor.compolybot.model

import org.springframework.data.annotation.Id

data class UserRating(
    val userId: Long,
    val username: String?,
    val chatId: Long,
    var rep: Long,
    @Id
    val id: String = "$chatId.$userId"
)
