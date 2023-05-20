package com.vitekkor.compolybot.service

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.network.serialization.GsonFactory
import com.google.gson.reflect.TypeToken
import com.vitekkor.compolybot.config.properties.BotConfigurationProperties
import com.vitekkor.compolybot.model.telegram.MediaGroup
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class TelegramApiService(private val ktorClient: HttpClient, botConfigurationProperties: BotConfigurationProperties) {
    private val botToken = botConfigurationProperties.telegramToken

    private val gson = GsonFactory.createForApiClient()

    fun sendMediaGroup(chatId: Long, mediaGroup: MediaGroup) = runBlocking {
        val responseType = object : TypeToken<Response<List<Message>>>() {}.type
        ktorClient.submitForm<String>(
            "https://api.telegram.org/bot$botToken/sendMediaGroup",
            formParameters = Parameters.build {
                append("chat_id", chatId.toString())
                append("media", gson.toJson(mediaGroup.media))
            }
        ).let { gson.fromJson<Response<List<Message>>>(it, responseType).result }
    }
}