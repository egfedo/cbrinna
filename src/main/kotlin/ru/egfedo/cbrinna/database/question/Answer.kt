package ru.egfedo.cbrinna.database.question

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    var text: String = "text",
    var correct: Boolean = false
)
