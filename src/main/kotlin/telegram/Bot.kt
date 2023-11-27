package telegram

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.core.UserDataMapImpl


// Uuh bot

object Bot {
    private const val TOKEN = "nuh uh"
    private val bot: TelegramBot = TelegramBot(TOKEN, "telegram.commands")

    suspend fun start() {
        bot.handleUpdates()
    }

}
