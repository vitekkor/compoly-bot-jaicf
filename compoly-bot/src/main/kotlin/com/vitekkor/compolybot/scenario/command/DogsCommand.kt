package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.builder.StateBuilder
import com.justai.jaicf.reactions.Reactions
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class DogsCommand(private val ktorClient: HttpClient) : BaseCommand() {
    override val name: String = "пёсик"
    override val description: String = "ПЁСИКИ!"

    override val coolDown: Duration = Duration.ofHours(4)
    override val coolDownMessage: String =
        "Товарищ, ваши пёсики закончились. Обновление запаса пёсиков происходит раз в 4 часа"

    private val catApiLink = "https://dog.ceo/api/breeds/image/random"

    private val noImage = "https://sun9-22.userapi.com/impg/9DSAvuiYG8-a8ZoTULK0c7qXa-Ze5EZD8jU0YA/-FzHoXGxfQM.jpg?" +
        "size=257x307&quality=96&sign=a9f6943997073aa917da6350453f2c3c&type=album"

    override fun StateBuilder<BotRequest, Reactions>.commandAction() {
        activators { commandActivator("пёсик", "песик", "dog") }
        action {
            val catImage = runBlocking {
                kotlin.runCatching {
                    ktorClient.get<DogApiResponse>(catApiLink).message
                }.getOrNull() ?: noImage
            }
            reactions.image(catImage)
        }
    }

    @Serializable
    internal data class DogApiResponse(
        val message: String,
        val status: String
    )
}
