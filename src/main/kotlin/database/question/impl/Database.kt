package database.question.dbimpl

import telegram.data.FrontendQuestion
import database.json.MapWrapper
import database.question.Question
import database.json.TreeSetAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import database.question.DatabaseInterface
import database.user.UserData
import io.FileParser
import java.io.File
import java.io.InputStream
import kotlin.math.min

/**
 * This db implementation is loaded/saved to json.
 *
 */
class Database(filename: String, prefix: String = "", dbID: String = ""): DatabaseInterface {
    private val questionList: MutableMap<Int, Question>
    private val questionSet: MutableSet<Question>
    private val adapter: JsonAdapter<MapWrapper>
    private val filename: String
    private val prefix: String
    private val dbID: String
    init {
        this.dbID = dbID
        this.prefix = prefix
        this.filename = filename
        val inputStream: InputStream = File(filename).inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        //println(inputString)
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(TreeSetAdapter()).build()
        this.adapter = moshi.adapter(MapWrapper::class.java)
        val output = adapter.fromJson(inputString)
        if (output != null) {
            this.questionList = output.map
            this.questionSet = output.map.values.toMutableSet()
            println("load ${output.map.size}")
//            println(output.set.toList()[output.set.size-1])
//            print(output.set)
        }
        else {
            this.questionList = mutableMapOf()
            this.questionSet = mutableSetOf()
        }
//        print(this.contents)
    }
    override fun addEntries(filename: String) : Int {
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

    override fun getRandomEntries(amount: Int) : MutableList<FrontendQuestion> {
        val correctAmt = min(amount, questionList.size)
        val tempList = questionList.values.toMutableList()
        val indices = (0..<questionList.size).toMutableList()
        indices.shuffle()
        println(indices)
        tempList.shuffle()
        val slice = indices.slice(0..<correctAmt)
        val result: MutableList<FrontendQuestion> = mutableListOf()
        for (i in slice) {
            val question = questionList[i]
            if (question != null) {
                val frontendQuestion = FrontendQuestion(question)
                frontendQuestion.id = "$prefix$i"
                frontendQuestion.originDB = dbID
                frontendQuestion.originID = "$i"
                result.add(frontendQuestion)
            }
        }
        return result
    }

    override fun getRandomEntries(amount: Int, userData: UserData, getMode: DatabaseInterface.GetMode) : MutableList<FrontendQuestion> {
        val correctAmt: Int
        val indices: MutableList<Int>
        if (getMode == DatabaseInterface.GetMode.All)
            return getRandomEntries(amount)

        if (getMode == DatabaseInterface.GetMode.Excluding) {
            val encounteredQuestions = userData.encounteredQuestions[dbID]
            if (encounteredQuestions == null) {
                println("Warning: unable to get entries from encountered questions db for $dbID")
                return mutableListOf()
            }
            correctAmt = min(amount, questionList.size - encounteredQuestions.size)
            indices = (0..<questionList.size).toMutableList()
            val realExclude = encounteredQuestions.map { it.toInt() }
            indices.removeAll(realExclude)
        }
        else {
            val wrongQuestions = userData.wrongAnswerList[dbID]
            if (wrongQuestions == null) {
                println("Warning: unable to get wrong questions db for $dbID")
                return mutableListOf()
            }
            correctAmt = min(amount, wrongQuestions.size)
            indices = wrongQuestions.map { it.toInt() }.toMutableList()
        }
        indices.shuffle()
        println(indices)
        val slice = indices.slice(0..<correctAmt)
        val result: MutableList<FrontendQuestion> = mutableListOf()
        for (i in slice) {
            val question = questionList[i]
            if (question != null) {
                val frontendQuestion = FrontendQuestion(question)
                frontendQuestion.id = "$prefix$i"
                frontendQuestion.originDB = dbID
                frontendQuestion.originID = "$i"
                result.add(frontendQuestion)
            }
        }
        return result
    }
    override fun size() = questionList.size

    override fun size(userData: UserData, getMode: DatabaseInterface.GetMode) : Int {
        return when (getMode) {
            DatabaseInterface.GetMode.All -> size()
            DatabaseInterface.GetMode.Excluding -> questionList.size - (userData.encounteredQuestions[dbID]?.size ?: 0)
            DatabaseInterface.GetMode.Only -> userData.wrongAnswerList[dbID]?.size ?: 0
        }
    }
    private fun save() {
        val file = File(filename)
        file.writeText("")
        val wrapper = MapWrapper(questionList)
        println("save ${wrapper.map.size}")
        file.printWriter().use { it.print(adapter.toJson(wrapper)) }
    }
}