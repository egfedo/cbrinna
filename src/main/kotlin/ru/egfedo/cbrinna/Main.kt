package ru.egfedo.cbrinna

import io.micronaut.context.ApplicationContext
import ru.egfedo.cbrinna.telegram.Bot

suspend fun main() {
    ApplicationContext.run().use {
        it.getBean(Bot::class.java).start()
    }
}
