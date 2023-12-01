package ru.egfedo.cbrinna.telegram

import eu.vendeli.tgbot.TelegramBot
import jakarta.inject.Named
import jakarta.inject.Singleton


// Uuh bot


@Singleton
class Bot (
    @Named("token")
    private val token: String
) {
    suspend fun start() {
        println("Token: " + token)
        TelegramBot(token, Bot::class.java.packageName).handleUpdates()
    }
}
