package telegram

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.core.UserDataMapImpl


// Uuh bot

object Bot {
    private const val TOKEN = "6508455841:AAF2Zq0MlY_X8hMlHlGjbkGwDqeMwzaCxG0" // revoked I was too silly to push it
    private val bot: TelegramBot = TelegramBot(TOKEN, "telegram.commands")

    suspend fun start() {
        bot.handleUpdates()
    }

}
