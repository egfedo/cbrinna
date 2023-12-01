package ru.egfedo.cbrinna.configuration

import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton

@Factory
class BotConfiguration {
    @Singleton
    @Named("token")
    fun telegramToken(): String {
        return "gay bowser"
    }
}
