package ru.egfedo.cbrinna.io

import ru.egfedo.cbrinna.database.question.Answer
import ru.egfedo.cbrinna.database.question.Question
import java.io.File
import java.util.*

object FileParser {
    enum class State {
        Idle, QuestionText, Answers, CorrectAnswers
    }
    private var state: State = State.Idle

    fun parse(path: String) : MutableSet<Question> {
        val file: File = File(path)
        val lines: MutableList<String> = ArrayList()
        file.forEachLine {
            if (it.isNotEmpty())
                lines.add(it)
        }
        for (j in 0..<lines.size) {
            var line = lines[j];
            var i = 0;
            while (i < line.length) {
                if (i > 1 && line[i] in " \n,." && line[i-1] == ' ') {
                    line = line.removeRange(i, i+1)
                }
                i++
            }
            lines[j] = line;
        }
        for (line in lines) {
//            println(line)
        }
        state = State.QuestionText
        var questions : MutableSet<Question> = mutableSetOf()
        var answers : MutableList<Answer> = mutableListOf()
        var num = 0
        for (i in lines.indices) {
            var text: String = ""

            if (state == State.QuestionText) {
                if (!lines[i].startsWith("Текст"))
                    continue
                text = lines[i+1]
                num++;
                state = State.Answers
            }
            if (state == State.Answers) {
                if((lines[i][0].isDigit() || lines[i][0] in "abcd") && lines[i][1] == '.') {
                    var answerText = lines[i + 1].removePrefix(" ")
                    for (i in 1..10) {
                        answerText = answerText.removePrefix(" ")
                    }
                    answers.add(Answer(answerText, false))
                }
                if(lines[i].startsWith("Отзыв"))
                    state = State.CorrectAnswers
            }
            if (state == State.CorrectAnswers) {
                if(lines[i].startsWith("Правильн")) {
                    var line = lines[i]

                    var question: Question = if (line.startsWith("Правильный ответ:")) {
                        if (!line.startsWith("Правильный ответ: ")) // If answers are in the next line
                            line = lines[i+1]
                        line = line.removePrefix("Правильный ответ: ")
                        val outputAnswers : TreeSet<Answer> = sortedSetOf()
                        var correctFlag = 0
                        for (answer in answers) {
                            if (answer.text in line) {
                                answer.correct = true
                                correctFlag += 1
                            }
                            outputAnswers.add(answer)
                        }
                        if (correctFlag != 1) throw RuntimeException("У вопроса ${questions.size+1} нет правильных ответов или несколько\n \"$line\"\n$outputAnswers")
                        if (outputAnswers.size != 4) println("У вопроса ${questions.size} не 4 ответа. Подозрительно.")
                        Question(text, outputAnswers)
//                        println(question)
                    }
                    else {
                        if (!line.startsWith("Правильные ответы: "))
                            line = lines[i+1]
                        line = line.removePrefix("Правильные ответы: ")
                        val outputAnswers : TreeSet<Answer> = sortedSetOf()
                        var correctFlag = 0
                        for (answer in answers) {
                            if (answer.text in line) {
                                answer.correct = true
                                correctFlag += 1
                            }
                            outputAnswers.add(answer)
                        }
                        if (correctFlag == 0) throw RuntimeException("У вопроса ${questions.size+1} нет правильных ответов\n \"$line\"\n$outputAnswers")
                        if (correctFlag == 1) println("У вопроса ${questions.size} с несколькими ответами всего 1 правильный ответ. Подозрительно.")
                        if (outputAnswers.size != 4) println("У вопроса ${questions.size} не 4 ответа. Подозрительно.")
//
                        Question(text, outputAnswers)
                    }
                    if(!questions.add(question)) {}
                        //print("Not added since it was already there: $question")
                    answers = mutableListOf()
                    state = State.QuestionText
                }
            }
        }
    return questions
    }
}
