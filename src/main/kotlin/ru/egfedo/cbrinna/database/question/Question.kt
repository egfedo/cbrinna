package ru.egfedo.cbrinna.database.question

import kotlinx.serialization.Serializable

/**
 * Sort of a backend Question. Has a java TreeSet for answers since
 * for some reason sortedSetOf() always return TreeSet and it needs
 * to be sorted for hash to work.
 * Hash allows to filter already existing questions with a set.
 */

@Serializable
class Question(
    var text: String,
    var answers: Set<Answer>,
    var type: Type = if (answers.count { it.correct } == 1) Type.OneAnswer else Type.MultipleAnswers
) {
    enum class Type {
        OneAnswer, MultipleAnswers
    }

    fun display(): String {
        return buildString {
            append("Вопрос: ")
            append(text)
            appendLine()
            appendLine()

            answers.forEachIndexed { i, a ->
                append(i + 1)
                append(". ")
                append(a.text)
                appendLine()
            }

            if (type == Type.OneAnswer) {
                appendLine()
                append("Выберите один правильный ответ")
                appendLine()
            } else {
                appendLine()
                append("Выберите один или несколько правильных ответов")
                appendLine()
            }
        }
    }

}
