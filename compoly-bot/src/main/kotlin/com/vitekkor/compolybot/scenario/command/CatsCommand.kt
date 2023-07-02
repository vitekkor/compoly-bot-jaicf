package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import com.vitekkor.compolybot.config.properties.CatApiConfigProperties
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CatsCommand(private val ktorClient: HttpClient, catApiConfigProperties: CatApiConfigProperties) : BaseCommand() {
    override val name: String = "котик"
    override val description: String = "КОТИКИ!"
    override val coolDown: Duration = Duration.ofHours(4)
    override val coolDownMessage: String =
        "Товарищ, ваши котики закончились. Обновление запаса котов происходит раз в 4 часа."

    private val catApiLink = "https://api.thecatapi.com/v1/images/search?api_key=${catApiConfigProperties.apiKey}"

    private val noImage = "https://sun9-22.userapi.com/impg/9DSAvuiYG8-a8ZoTULK0c7qXa-Ze5EZD8jU0YA/-FzHoXGxfQM.jpg?" +
        "size=257x307&quality=96&sign=a9f6943997073aa917da6350453f2c3c&type=album"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("котик", "cat") }
        action {
            val catImage = runBlocking {
                kotlin.runCatching {
                    ktorClient.get<List<CatApiResponse>>(catApiLink).firstOrNull()?.url
                }.getOrNull() ?: noImage
            }
            reactions.image(catImage)
        }
    }

    @Serializable
    internal data class CatApiResponse(
        val breeds: List<JsonObject>,
        val id: String,
        val url: String,
        val width: Int,
        val height: Int
    )
}
