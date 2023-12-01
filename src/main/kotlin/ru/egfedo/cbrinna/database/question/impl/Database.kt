package database.question.dbimpl

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.egfedo.cbrinna.database.question.DatabaseInterface
import ru.egfedo.cbrinna.database.question.Question
import ru.egfedo.cbrinna.database.user.UserData
import ru.egfedo.cbrinna.io.FileParser
import ru.egfedo.cbrinna.telegram.data.FrontendQuestion
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * This db implementation is loaded/saved to json.
 *
 */
class Database(
    val filename: String,
    val prefix: String,
    val dbID: String
) : DatabaseInterface {
    private val questionList: MutableMap<Int, Question> = mutableMapOf()
    private val questionSet: MutableSet<Question> = mutableSetOf()

    fun init(): Database {
        val output = Json.decodeFromString<Map<Int, Question>>(Path(filename).readText())

        this.questionList.clear()
        this.questionList += output.toMutableMap()

        this.questionSet.clear()
        this.questionSet += output.values.toMutableSet()

        return this
    }

    override fun addEntries(filename: String): Int {
        val prev = questionList.size
//        println("old ${contents.size}")
        val parsed = FileParser.parse(filename)
        for (element in parsed)
            if (!questionSet.contains(element)) {
                questionSet.add(element)
                questionList[questionList.size] = element
                println(element)
            }
        val diff = questionList.size - prev
//        println("new ${contents.size}")
        save()              // Never ever tell me that's inefficient
        return diff
    }

    override fun getEntries(): Sequence<FrontendQuestion> {
        return questionList.values
            .asSequence()
            .mapIndexed { i, question ->
                val frontendQuestion = FrontendQuestion(question)
                frontendQuestion.id = "$prefix$i"
                frontendQuestion.originDB = dbID
                frontendQuestion.originID = "$i"
                frontendQuestion
            }
    }

    override fun getEntries(
        userData: UserData,
        getMode: DatabaseInterface.GetMode
    ): Sequence<FrontendQuestion> {
        val entries = getEntries()

        return when (getMode) {
            DatabaseInterface.GetMode.All -> entries
            DatabaseInterface.GetMode.Excluding -> {
                val encounteredQuestions = userData.encounteredQuestions[dbID] ?: emptySet()
                entries.filter { question -> question.id !in encounteredQuestions }
            }

            DatabaseInterface.GetMode.Only -> {
                val wrongQuestions = userData.wrongAnswerList[dbID] ?: emptySet()
                entries.filter { question -> question.id in wrongQuestions }
            }
        }
    }

    override fun size() = questionList.size

    override fun size(userData: UserData, getMode: DatabaseInterface.GetMode): Int {
        return when (getMode) {
            DatabaseInterface.GetMode.All -> size()
            DatabaseInterface.GetMode.Excluding -> questionList.size - (userData.encounteredQuestions[dbID]?.size ?: 0)
            DatabaseInterface.GetMode.Only -> userData.wrongAnswerList[dbID]?.size ?: 0
        }
    }

    private fun save() {
        Path(filename).writeText(Json.encodeToString(questionList))
    }
}
