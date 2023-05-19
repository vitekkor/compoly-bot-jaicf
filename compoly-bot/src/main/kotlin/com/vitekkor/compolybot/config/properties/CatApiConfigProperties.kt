package com.vitekkor.compolybot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "cat-api")
data class CatApiConfigProperties(val apiKey: String)
