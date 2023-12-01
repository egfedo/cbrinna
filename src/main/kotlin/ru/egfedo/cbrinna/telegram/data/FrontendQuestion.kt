package ru.egfedo.cbrinna.telegram.data

import ru.egfedo.cbrinna.database.question.Question
import ru.egfedo.cbrinna.database.question.Answer

/**
 * Question to actually be displayed to user.
 * Has a constructor to convert it from Question.
 * @see Question
 */
class FrontendQuestion () {
    enum class Type {
        OneAnswer, MultipleAnswers
    }
    var text: String
    var answers: MutableList<Answer>
    var type: Type
    var id: String
    var originDB: String
    var originID: String
    init {
        this.text = ""
        this.answers = mutableListOf()
        this.type = Type.OneAnswer
        this.id = ""
        this.originID = ""
        this.originDB = ""
    }
    constructor(question: Question) : this() {
        this.text = question.text
        this.answers = question.answers.toMutableList()
        this.answers.shuffle()
        this.type = if (question.type == Question.Type.OneAnswer) Type.OneAnswer
        else Type.MultipleAnswers
    }
    override fun toString() : String {
        val builder = StringBuilder();
        builder.append("*Вопрос:* ").append(formatToMD(text)).append(formatToMD(" \\[№$id\\]")).append("\n\n")
        var i = 1;
        for (a in answers) {
            builder.append("*").append(i).append("\\.* ").append(formatToMD(a.text))
//            if (a.correct)
//                builder.append(" * ")
            builder.append("\n")
            i++
        }
        if (type == Type.OneAnswer)
            builder.append("\nВыберите *один* правильный ответ\n")
        else
            builder.append("\nВыберите *один или несколько* правильных ответов\n")
        return builder.toString()
    }

    private fun formatToMD(string: String) : String {
        return string.replace(",", "\\,").replace("-", "\\-").replace(".", "\\.").replace("(", "\\(").replace(")", "\\)")
    }
}
