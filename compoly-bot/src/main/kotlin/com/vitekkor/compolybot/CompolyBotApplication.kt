package com.vitekkor.compolybot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringBootApplication
class CompolyBotApplication

object Dev {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("spring.profiles.active", "dev")
        System.setProperty("log.dir", "compoly-bot/logs")
        com.vitekkor.compolybot.main(args)
    }
}

fun main(args: Array<String>) {
    runApplication<CompolyBotApplication>(*args)
}
