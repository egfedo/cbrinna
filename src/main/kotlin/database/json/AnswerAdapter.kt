package database.json

import database.question.Answer
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * I was too lazy to actually learn sql
 */
class AnswerAdapter {

    @ToJson
    fun toJson(answer: Answer) : String {
        return "${answer.correct} ${answer.text}"
    }

    @FromJson
    fun fromJson(str: String) : Answer {
        var string = str;
        val out = Answer()
        if (str.startsWith("true")) {
            out.correct = true
            string = string.removePrefix("true ")
        }
        else {
            out.correct = false
            string = string.removePrefix("false ")
        }
        out.text = string
        return out
    }
}